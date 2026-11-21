import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Loader2, Plus, RotateCw } from 'lucide-react'
import { type FormEvent, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { authoringApi, isConflict, type Grade, type SkeletonDraft } from '../api/authoring'
import { isNotFound } from '../api'
import { Button, Card, CardHeader, EmptyState, ErrorState, Field, Input, Mono, Notice, PageHeader, Skeleton } from '../components/ui'

const draftKey = (curriculumId: string) => ['authoring', 'draft', curriculumId] as const

/**
 * 커리큘럼 편집(저작). 뼈대 초안의 제목·설명, 학년, 학기를 고친다.
 *
 * 이 화면은 처음 읽은 revision 을 들고 있다가 고칠 때마다 보낸다(낙관적 락).
 * 다른 탭이나 다른 사람이 먼저 고치면 409 를 받고, 다시 불러와야 고칠 수 있다.
 * 그래서 창을 다시 볼 때 저절로 새로 읽지 않는다. 저절로 읽으면 409 를 볼 일이 없다.
 */
export function AuthoringPage() {
  const { curriculumId } = useParams()
  return (
    <div className="space-y-6">
      <PageHeader
        title="커리큘럼 편집"
        description="초안을 고칩니다. 배포는 초안이 아니라 공개한 판을 읽습니다. 탭 두 개로 같은 커리큘럼을 열어 고쳐 보면 낙관적 락을 볼 수 있습니다."
      />
      <OpenOrCreate />
      {curriculumId ? <DraftEditor key={curriculumId} curriculumId={curriculumId} /> : null}
    </div>
  )
}

/** 커리큘럼 ID로 열거나 새로 만든다. 목록 API 는 아직 없다 */
function OpenOrCreate() {
  const navigate = useNavigate()
  const [openId, setOpenId] = useState('')
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const create = useMutation({
    mutationFn: () => authoringApi.create(title.trim(), description.trim()),
    onSuccess: (created) => {
      setTitle('')
      setDescription('')
      navigate(`/authoring/${created.curriculumId}`)
    },
  })

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <Card>
        <CardHeader title="커리큘럼 열기" description="커리큘럼 ID를 넣습니다. 예: CUR001" />
        <form
          className="flex gap-2 p-5"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            if (openId.trim()) navigate(`/authoring/${openId.trim().toUpperCase()}`)
          }}
        >
          <Input value={openId} onChange={(e) => setOpenId(e.target.value)} placeholder="CUR001" aria-label="커리큘럼 ID" />
          <Button type="submit" variant="secondary" disabled={!openId.trim()}>
            열기
          </Button>
        </form>
      </Card>

      <Card>
        <CardHeader title="새 커리큘럼" description="ID는 시스템이 CUR001 부터 순서대로 매깁니다." />
        <form
          className="space-y-3 p-5"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            create.mutate()
          }}
        >
          <div className="grid gap-3 sm:grid-cols-2">
            <Field label="제목">
              <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="중등 교육 과정" />
            </Field>
            <Field label="설명">
              <Input value={description} onChange={(e) => setDescription(e.target.value)} />
            </Field>
          </div>
          {create.error && <Notice tone="danger">{create.error.message}</Notice>}
          <Button type="submit" disabled={!title.trim() || create.isPending}>
            {create.isPending ? <Loader2 className="size-4 animate-spin" /> : <Plus className="size-4" />}
            만들기
          </Button>
        </form>
      </Card>
    </div>
  )
}

function DraftEditor({ curriculumId }: { curriculumId: string }) {
  const client = useQueryClient()
  const draft = useQuery({
    queryKey: draftKey(curriculumId),
    queryFn: () => authoringApi.get(curriculumId),
    // 들고 있는 revision 이 저절로 새것으로 바뀌지 않게 한다(위 설명)
    refetchOnWindowFocus: false,
    staleTime: Infinity,
    retry: false,
  })
  // 409 를 받으면 여기에 남긴다. 다시 불러오기 전까지 보여 준다
  const [conflict, setConflict] = useState<string | null>(null)

  const reload = () => {
    setConflict(null)
    void client.invalidateQueries({ queryKey: draftKey(curriculumId) })
  }

  if (draft.isPending) {
    return (
      <Card>
        <div className="space-y-3 p-5">
          <Skeleton className="h-6 w-48" />
          <Skeleton className="h-24 w-full" />
        </div>
      </Card>
    )
  }
  if (draft.error) {
    return (
      <Card>
        {isNotFound(draft.error) ? (
          <EmptyState title={`${curriculumId} 커리큘럼이 없습니다`} description="ID를 확인하거나 새로 만드세요." />
        ) : (
          <ErrorState error={draft.error} onRetry={reload} />
        )}
      </Card>
    )
  }

  return (
    <EditorBody
      key={draft.data.revision}
      draft={draft.data}
      conflict={conflict}
      onConflict={setConflict}
      onReload={reload}
    />
  )
}

/**
 * 고치기 하나가 성공하면 그 응답의 revision 을 이 화면의 새 revision 으로 삼는다.
 * 내용은 서버에서 다시 읽는다. 그래야 화면과 서버가 같은 revision 을 본다
 */
function EditorBody({
  draft,
  conflict,
  onConflict,
  onReload,
}: {
  draft: SkeletonDraft
  conflict: string | null
  onConflict: (message: string) => void
  onReload: () => void
}) {
  const client = useQueryClient()
  const [title, setTitle] = useState(draft.title)
  const [description, setDescription] = useState(draft.description ?? '')
  const [gradeTitle, setGradeTitle] = useState('')

  // 성공하면 다시 읽고, 409 면 충돌로 남긴다. 그 밖의 오류는 각 버튼 아래에 보여 준다
  const handlers = {
    onSuccess: () => void client.invalidateQueries({ queryKey: draftKey(draft.curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  }
  const edit = useMutation({
    mutationFn: () => authoringApi.edit(draft.curriculumId, title.trim(), description.trim(), draft.revision),
    ...handlers,
  })
  const addGrade = useMutation({
    mutationFn: () => authoringApi.addGrade(draft.curriculumId, gradeTitle.trim(), draft.revision),
    ...handlers,
  })

  const locked = conflict !== null

  return (
    <div className="space-y-6">
      {conflict && (
        <Notice tone="warning" title="다른 사람이 먼저 고쳤습니다">
          <p>{conflict}</p>
          <p>고친 내용은 저장되지 않았습니다. 다시 불러와 최신 내용을 본 뒤 고치세요.</p>
          <Button variant="secondary" className="mt-2" onClick={onReload}>
            <RotateCw className="size-4" />
            다시 불러오기
          </Button>
        </Notice>
      )}

      <Card>
        <CardHeader
          title={
            <span className="flex items-center gap-2">
              <Mono>{draft.curriculumId}</Mono>
              <span>{draft.title}</span>
            </span>
          }
          description="뼈대 초안: 커리큘럼 정보, 학년, 학기"
          action={
            <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-600" title="낙관적 락 번호. 고칠 때 함께 보낸다">
              revision {draft.revision}
            </span>
          }
        />
        <form
          className="space-y-3 p-5"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            edit.mutate()
          }}
        >
          <div className="grid gap-3 sm:grid-cols-2">
            <Field label="제목">
              <Input value={title} onChange={(e) => setTitle(e.target.value)} disabled={locked} />
            </Field>
            <Field label="설명">
              <Input value={description} onChange={(e) => setDescription(e.target.value)} disabled={locked} />
            </Field>
          </div>
          {edit.error && !isConflict(edit.error) && <Notice tone="danger">{edit.error.message}</Notice>}
          <Button type="submit" disabled={locked || !title.trim() || edit.isPending}>
            저장 (revision {draft.revision} 기준)
          </Button>
        </form>
      </Card>

      <Card>
        <CardHeader title="학년과 학기" description={`학년 ${draft.grades.length}개`} />
        <div className="space-y-4 p-5">
          {draft.grades.length === 0 && <p className="text-sm text-slate-500">아직 학년이 없습니다.</p>}
          {draft.grades.map((grade) => (
            <GradeRow key={grade.id} draft={draft} grade={grade} locked={locked} handlers={handlers} />
          ))}

          <form
            className="flex gap-2 border-t border-slate-100 pt-4"
            onSubmit={(e: FormEvent) => {
              e.preventDefault()
              addGrade.mutate()
            }}
          >
            <Input value={gradeTitle} onChange={(e) => setGradeTitle(e.target.value)} placeholder="새 학년 이름 (예: 1학년)" disabled={locked} />
            <Button type="submit" variant="secondary" disabled={locked || !gradeTitle.trim() || addGrade.isPending}>
              <Plus className="size-4" />
              학년 추가
            </Button>
          </form>
          {addGrade.error && !isConflict(addGrade.error) && <Notice tone="danger">{addGrade.error.message}</Notice>}
        </div>
      </Card>
    </div>
  )
}

function GradeRow({
  draft,
  grade,
  locked,
  handlers,
}: {
  draft: SkeletonDraft
  grade: Grade
  locked: boolean
  handlers: { onSuccess: () => void; onError: (error: Error) => void }
}) {
  const [semesterTitle, setSemesterTitle] = useState('')
  const addSemester = useMutation({
    mutationFn: () => authoringApi.addSemester(draft.curriculumId, grade.id, semesterTitle.trim(), draft.revision),
    ...handlers,
  })

  return (
    <div className="rounded-lg border border-slate-200 p-4">
      <div className="flex items-baseline justify-between gap-3">
        <p className="font-medium text-slate-900">{grade.title}</p>
        <Mono className="text-xs text-slate-400">{grade.id}</Mono>
      </div>
      <ul className="mt-2 space-y-1 pl-4">
        {grade.semesters.length === 0 && <li className="text-sm text-slate-500">학기 없음</li>}
        {grade.semesters.map((semester) => (
          <li key={semester.id} className="flex items-baseline justify-between gap-3 text-sm text-slate-700">
            <span>· {semester.title}</span>
            <Mono className="text-xs text-slate-400">{semester.id}</Mono>
          </li>
        ))}
      </ul>
      <form
        className="mt-3 flex gap-2"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          addSemester.mutate()
        }}
      >
        <Input value={semesterTitle} onChange={(e) => setSemesterTitle(e.target.value)} placeholder="새 학기 이름 (예: 1학기)" disabled={locked} />
        <Button type="submit" variant="ghost" disabled={locked || !semesterTitle.trim() || addSemester.isPending}>
          <Plus className="size-4" />
          학기 추가
        </Button>
      </form>
      {addSemester.error && !isConflict(addSemester.error) && <Notice tone="danger" className="mt-2">{addSemester.error.message}</Notice>}
    </div>
  )
}
