import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import clsx from 'clsx'
import { BookOpen, CalendarRange, ChevronRight, GraduationCap, Loader2, Pencil, RotateCw, Trash2, Upload } from 'lucide-react'
import { type FormEvent, type ReactNode, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { isNotFound } from '../../api'
import {
  authoringApi,
  isConflict,
  type Grade,
  type MoveDirection,
  type Semester,
  type SkeletonDraft,
  type SubjectDraft,
  type VersionSummary,
} from '../../api/authoring'
import { Button, Card, CardHeader, EmptyState, ErrorState, Input, Mono, Notice, Skeleton, Table, Td, Th } from '../../components/ui'
import { AddInline, draftKey, type Handlers, LevelTag, MoveButtons, subjectsKey, versionsKey } from './parts'
import { formatDateTime } from '../../lib/format'
import { PublishBadge } from './PublishBadge'


type Tab = 'structure' | 'info' | 'versions'
const TABS: { key: Tab; label: string }[] = [
  { key: 'structure', label: '구성' },
  { key: 'info', label: '기본 정보' },
  { key: 'versions', label: '판 기록' },
]


/**
 * 커리큘럼 상세. 여기서 초안을 고친다.
 *
 * 화면은 처음 읽은 revision 을 들고 있다가 고칠 때마다 보낸다(낙관적 락).
 * 다른 사람이 먼저 고치면 409 를 받고, 다시 불러와야 이어서 고칠 수 있다.
 * 그래서 창을 다시 볼 때 저절로 새로 읽지 않는다. 저절로 읽으면 고치던 입력이 말없이 사라지고,
 * 남이 먼저 고친 것도 알 수 없다
 */
export function CurriculumDetailPage() {
  const { curriculumId = '' } = useParams()
  const client = useQueryClient()
  const draft = useQuery({
    queryKey: draftKey(curriculumId),
    queryFn: () => authoringApi.get(curriculumId),
    refetchOnWindowFocus: false,
    staleTime: Infinity,
    retry: false,
  })
  // 과목과 판 기록은 머리(공개 버튼, 공개 상태)와 탭이 함께 쓴다. 그래서 페이지에서 읽는다
  const subjects = useQuery({
    queryKey: subjectsKey(curriculumId),
    queryFn: () => authoringApi.listSubjects(curriculumId),
    refetchOnWindowFocus: false,
    staleTime: Infinity,
  })
  const versions = useQuery({
    queryKey: versionsKey(curriculumId),
    queryFn: () => authoringApi.listVersions(curriculumId),
    refetchOnWindowFocus: false,
  })
  const [conflict, setConflict] = useState<string | null>(null)
  const [tab, setTab] = useState<Tab>('structure')

  const reload = () => {
    setConflict(null)
    void client.invalidateQueries({ queryKey: draftKey(curriculumId) })
    void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) })
  }
  const handlers: Handlers = {
    onSuccess: () => void client.invalidateQueries({ queryKey: draftKey(curriculumId) }),
    onError: (error) => {
      if (isConflict(error)) setConflict(error.message)
    },
  }

  return (
    <div className="space-y-6">
      <nav className="flex items-center gap-1 text-sm text-slate-500" aria-label="경로">
        <Link to="/authoring" className="hover:text-slate-800">
          커리큘럼
        </Link>
        <ChevronRight className="size-4" />
        <Mono className="text-slate-700">{curriculumId}</Mono>
      </nav>

      {draft.isPending ? (
        <Card>
          <div className="space-y-3 p-5">
            <Skeleton className="h-7 w-64" />
            <Skeleton className="h-32 w-full" />
          </div>
        </Card>
      ) : draft.error ? (
        <Card>
          {isNotFound(draft.error) ? (
            <EmptyState
              title={`${curriculumId} 커리큘럼이 없습니다`}
              action={
                <Link to="/authoring">
                  <Button variant="secondary">목록으로</Button>
                </Link>
              }
            />
          ) : (
            <ErrorState error={draft.error} onRetry={reload} />
          )}
        </Card>
      ) : (
        <>
          <DetailHeader
            draft={draft.data}
            subjects={subjects.data}
            lastVersion={versions.data?.[0]}
            locked={conflict !== null}
            onConflict={setConflict}
          />

          {conflict && (
            <Notice tone="warning" title="다른 사람이 먼저 고쳤습니다">
              <p>{conflict}</p>
              <p>방금 고친 내용은 저장되지 않았습니다. 다시 불러와 최신 내용을 본 뒤 고치세요.</p>
              <Button variant="secondary" className="mt-2" onClick={reload}>
                <RotateCw className="size-4" />
                다시 불러오기
              </Button>
            </Notice>
          )}

          <div className="flex gap-1 border-b border-slate-200">
            {TABS.map((t) => (
              <button
                key={t.key}
                type="button"
                onClick={() => setTab(t.key)}
                className={clsx(
                  '-mb-px border-b-2 px-4 py-2.5 text-sm font-medium transition',
                  tab === t.key ? 'border-brand-600 text-brand-700' : 'border-transparent text-slate-500 hover:text-slate-800',
                )}
              >
                {t.label}
              </button>
            ))}
          </div>

          {/* revision 이 바뀌면(다시 읽으면) 안쪽 입력 상태를 처음으로 돌린다 */}
          <div key={draft.data.revision}>
            {tab === 'structure' && <StructureTab draft={draft.data} locked={conflict !== null} handlers={handlers} />}
            {tab === 'info' && <InfoTab draft={draft.data} locked={conflict !== null} handlers={handlers} />}
            {tab === 'versions' && <VersionsTab versions={versions.data} draft={draft.data} />}
          </div>
        </>
      )}
    </div>
  )
}

/** 초안이 가장 최근 판을 만든 초안과 같은가. revision 이 하나라도 다르면 공개 뒤에 고친 것이다 */
function sameAsVersion(draft: SkeletonDraft, subjects: SubjectDraft[], version: VersionSummary): boolean {
  if (draft.revision !== version.skeletonRevision) return false
  const published = version.subjectRevisions
  if (Object.keys(published).length !== subjects.length) return false
  return subjects.every((s) => published[s.id] === s.revision)
}

function DetailHeader({
  draft,
  subjects,
  lastVersion,
  locked,
  onConflict,
}: {
  draft: SkeletonDraft
  subjects?: SubjectDraft[]
  lastVersion?: VersionSummary
  locked: boolean
  onConflict: (message: string) => void
}) {
  const client = useQueryClient()
  const changed = lastVersion !== undefined && subjects !== undefined && !sameAsVersion(draft, subjects, lastVersion)
  // 공개는 화면이 지금 보고 있는 revision 들을 그대로 보낸다. 그사이 누가 고쳤으면 서버가 409 로 막는다
  const publish = useMutation({
    mutationFn: () =>
      authoringApi.publish(
        draft.curriculumId,
        draft.revision,
        Object.fromEntries((subjects ?? []).map((s) => [s.id, s.revision])),
      ),
    onSuccess: () => void client.invalidateQueries({ queryKey: versionsKey(draft.curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  const confirmPublish = () => {
    const next = (lastVersion?.version ?? 0) + 1
    const count = subjects?.length ?? 0
    if (window.confirm(`지금 보이는 내용(과목 ${count}개)으로 판 ${next}을 공개합니다.\n배포는 이 판을 읽습니다.`)) publish.mutate()
  }

  return (
    <div className="flex flex-wrap items-start justify-between gap-4">
      <div>
        <div className="flex items-center gap-2">
          <h1 className="text-2xl font-semibold tracking-tight">{draft.title}</h1>
          <PublishBadge version={lastVersion?.version} changedSincePublish={changed} />
        </div>
        <p className="mt-1 flex items-center gap-2 text-sm text-slate-500">
          <Mono>{draft.curriculumId}</Mono>
          <span>·</span>
          <span title="낙관적 락 번호. 고칠 때마다 1씩 오르고, 고칠 때 이 값을 함께 보낸다">revision {draft.revision}</span>
        </p>
      </div>
      <div className="flex flex-col items-end gap-2">
        <Button disabled={locked || subjects === undefined || publish.isPending} onClick={confirmPublish}>
          {publish.isPending ? <Loader2 className="size-4 animate-spin" /> : <Upload className="size-4" />}
          공개
        </Button>
        {publish.error && !isConflict(publish.error) && (
          <Notice tone="danger" className="max-w-sm">
            {publish.error.message}
          </Notice>
        )}
        {publish.isSuccess && <p className="text-xs text-emerald-700">판 {publish.data.version}을 공개했습니다</p>}
      </div>
    </div>
  )
}

// ─── 구성: 학년 > 학기 > 과목 ──────────────────────────────────

function StructureTab({ draft, locked, handlers }: { draft: SkeletonDraft; locked: boolean; handlers: Handlers }) {
  // 페이지가 읽은 것과 같은 키라 다시 부르지 않고 같은 값을 쓴다
  const subjects = useQuery({
    queryKey: subjectsKey(draft.curriculumId),
    queryFn: () => authoringApi.listSubjects(draft.curriculumId),
    refetchOnWindowFocus: false,
    staleTime: Infinity,
  })
  const addGrade = useMutation({
    mutationFn: (title: string) => authoringApi.addGrade(draft.curriculumId, title, draft.revision),
    ...handlers,
  })

  return (
    <div className="space-y-4">
      {/* 학년 추가는 맨 위에 둔다. 학년이 많아져도 스크롤하지 않고 찾을 수 있다 */}
      <div className="flex flex-wrap items-start justify-between gap-3">
        <p className="pt-2 text-sm text-slate-500">
          학년 {draft.grades.length}개 · 학년 안에 학기, 학기 안에 과목이 들어갑니다
        </p>
        <AddInline
          label="학년 추가"
          placeholder="학년 이름 (예: 1학년)"
          variant="primary"
          disabled={locked}
          pending={addGrade.isPending}
          error={addGrade.error && !isConflict(addGrade.error) ? addGrade.error.message : null}
          onSubmit={(title) => addGrade.mutate(title)}
        />
      </div>

      {draft.grades.length === 0 && (
        <Card>
          <EmptyState title="아직 학년이 없습니다" description="오른쪽 위 [학년 추가]로 첫 학년을 만드세요." />
        </Card>
      )}
      {draft.grades.map((grade) => (
        <GradeCard key={grade.id} draft={draft} grade={grade} subjects={subjects.data ?? []} locked={locked} handlers={handlers} />
      ))}
    </div>
  )
}

function GradeCard({
  draft,
  grade,
  subjects,
  locked,
  handlers,
}: {
  draft: SkeletonDraft
  grade: Grade
  subjects: SubjectDraft[]
  locked: boolean
  handlers: Handlers
}) {
  const addSemester = useMutation({
    mutationFn: (title: string) => authoringApi.addSemester(draft.curriculumId, grade.id, title, draft.revision),
    ...handlers,
  })
  // 연필을 누르면 머리 부분이 입력칸으로 바뀐다
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(grade.title)
  const [description, setDescription] = useState(grade.description ?? '')
  const editGrade = useMutation({
    mutationFn: () => authoringApi.editGrade(draft.curriculumId, grade.id, title.trim(), description.trim(), draft.revision),
    ...handlers,
  })
  const moveGrade = useMutation({
    mutationFn: (direction: MoveDirection) => authoringApi.moveGrade(draft.curriculumId, grade.id, direction, draft.revision),
    ...handlers,
  })
  const gradeIndex = draft.grades.findIndex((g) => g.id === grade.id)
  const removeGrade = useMutation({
    mutationFn: () => authoringApi.removeGrade(draft.curriculumId, grade.id, draft.revision),
    ...handlers,
  })
  const confirmRemove = () => {
    // 학기는 학년 안에 있어서 함께 지워진다. 그 사실을 먼저 알린다
    const semesters = grade.semesters.length > 0 ? `
학기 ${grade.semesters.length}개도 함께 지워집니다.` : ''
    if (window.confirm(`'${grade.title}' 학년을 지웁니다.${semesters}`)) removeGrade.mutate()
  }

  return (
    <Card>
      {editing ? (
        <form
          className="space-y-2 border-b border-slate-100 px-5 py-4"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            editGrade.mutate()
          }}
        >
          <div className="grid gap-2 sm:grid-cols-2">
            <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="학년 이름" aria-label="학년 이름" autoFocus disabled={locked} />
            <Input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" aria-label="학년 설명" disabled={locked} />
          </div>
          {editGrade.error && !isConflict(editGrade.error) && <Notice tone="danger">{editGrade.error.message}</Notice>}
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setEditing(false)}>
              취소
            </Button>
            <Button type="submit" disabled={locked || !title.trim() || editGrade.isPending}>
              {editGrade.isPending && <Loader2 className="size-4 animate-spin" />}
              저장
            </Button>
          </div>
        </form>
      ) : (
        <CardHeader
          title={
            <span className="flex items-center gap-2">
              <span className="grid size-7 place-items-center rounded-lg bg-brand-50 text-brand-700">
                <GraduationCap className="size-4" />
              </span>
              <LevelTag tone="grade">학년</LevelTag>
              <span>{grade.title}</span>
            </span>
          }
          description={grade.description ? `${grade.description} · 학기 ${grade.semesters.length}개` : `학기 ${grade.semesters.length}개`}
          action={
            <div className="flex items-center gap-1">
              <Mono className="mr-2 hidden text-xs text-slate-400 sm:inline">{grade.id}</Mono>
              <MoveButtons
                index={gradeIndex}
                count={draft.grades.length}
                disabled={locked || moveGrade.isPending}
                label="학년"
                onMove={(direction) => moveGrade.mutate(direction)}
              />
              <Button variant="ghost" className="px-2" disabled={locked} onClick={() => setEditing(true)} title="학년 수정" aria-label="학년 수정">
                <Pencil className="size-4" />
              </Button>
              <Button
                variant="ghost"
                className="px-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700"
                disabled={locked || removeGrade.isPending}
                onClick={confirmRemove}
                title="학년 삭제"
                aria-label="학년 삭제"
              >
                <Trash2 className="size-4" />
              </Button>
            </div>
          }
        />
      )}
      {removeGrade.error && !isConflict(removeGrade.error) && (
        <Notice tone="danger" className="m-4">
          {removeGrade.error.message}
        </Notice>
      )}
      {/* 학기는 학년 안에 들어 있다. 왼쪽 선으로 안쪽 단계임을 보인다 */}
      <div className="space-y-2 py-4 pr-4 pl-8">
        <div className="space-y-2 border-l-2 border-brand-100 pl-4">
          {grade.semesters.length === 0 && <p className="py-1 text-sm text-slate-400">아직 학기가 없습니다</p>}
          {grade.semesters.map((semester) => (
            <SemesterRow
              key={semester.id}
              draft={draft}
              grade={grade}
              semester={semester}
              subjects={subjects.filter((subject) => subject.semesterId === semester.id)}
              locked={locked}
              handlers={handlers}
            />
          ))}
          <AddInline
          label="학기 추가"
          placeholder="학기 이름 (예: 1학기)"
          variant="ghost"
          disabled={locked}
          pending={addSemester.isPending}
          error={addSemester.error && !isConflict(addSemester.error) ? addSemester.error.message : null}
          onSubmit={(title) => addSemester.mutate(title)}
          />
        </div>
      </div>
    </Card>
  )
}

/** 학기 한 줄. 연필을 누르면 이름과 설명을 고친다. 과목은 따로 저장하는 초안이라 아직 없다(과목 만들기는 다음 기능) */
function SemesterRow({
  draft,
  grade,
  semester,
  subjects,
  locked,
  handlers,
}: {
  draft: SkeletonDraft
  grade: Grade
  semester: Semester
  subjects: SubjectDraft[]
  locked: boolean
  handlers: Handlers
}) {
  const client = useQueryClient()
  // 과목 만들기는 뼈대를 고치지 않는다. 그래서 뼈대 revision 을 보내지 않고, 과목 목록만 다시 읽는다
  const createSubject = useMutation({
    mutationFn: (title: string) => authoringApi.createSubject(draft.curriculumId, semester.id, title),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(draft.curriculumId) }),
  })
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(semester.title)
  const [description, setDescription] = useState(semester.description ?? '')
  const editSemester = useMutation({
    mutationFn: () =>
      authoringApi.editSemester(draft.curriculumId, grade.id, semester.id, title.trim(), description.trim(), draft.revision),
    ...handlers,
  })
  // 과목이 남아 있으면 서버가 422 로 막는다(과목이 갈 곳을 잃지 않게). 그 이유를 아래에 보여 준다
  const moveSemester = useMutation({
    mutationFn: (direction: MoveDirection) =>
      authoringApi.moveSemester(draft.curriculumId, grade.id, semester.id, direction, draft.revision),
    ...handlers,
  })
  const semesterIndex = grade.semesters.findIndex((s) => s.id === semester.id)
  const removeSemester = useMutation({
    mutationFn: () => authoringApi.removeSemester(draft.curriculumId, grade.id, semester.id, draft.revision),
    ...handlers,
  })
  const confirmRemove = () => {
    if (window.confirm(`'${semester.title}' 학기를 지웁니다.`)) removeSemester.mutate()
  }

  if (editing) {
    return (
      <form
        className="space-y-2 rounded-lg border border-slate-200 bg-slate-50 px-4 py-3"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          editSemester.mutate()
        }}
      >
        <div className="grid gap-2 sm:grid-cols-2">
          <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="학기 이름" aria-label="학기 이름" autoFocus disabled={locked} />
          <Input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" aria-label="학기 설명" disabled={locked} />
        </div>
        {editSemester.error && !isConflict(editSemester.error) && <Notice tone="danger">{editSemester.error.message}</Notice>}
        <div className="flex justify-end gap-2">
          <Button variant="ghost" onClick={() => setEditing(false)}>
            취소
          </Button>
          <Button type="submit" disabled={locked || !title.trim() || editSemester.isPending}>
            {editSemester.isPending && <Loader2 className="size-4 animate-spin" />}
            저장
          </Button>
        </div>
      </form>
    )
  }

  return (
    <div className="rounded-lg border border-slate-200 bg-slate-50">
      <div className="flex items-center justify-between gap-3 px-4 py-2.5">
        <div className="flex items-center gap-2.5">
          <CalendarRange className="size-4 text-slate-400" />
          <LevelTag tone="semester">학기</LevelTag>
          <div>
            <p className="text-sm font-medium text-slate-800">{semester.title}</p>
            <p className="text-xs text-slate-500">{semester.description ? `${semester.description} · 과목 ${subjects.length}개` : `과목 ${subjects.length}개`}</p>
          </div>
        </div>
        <div className="flex items-center gap-1">
          <Mono className="mr-2 hidden text-xs text-slate-400 sm:inline">{semester.id}</Mono>
          <MoveButtons
            index={semesterIndex}
            count={grade.semesters.length}
            disabled={locked || moveSemester.isPending}
            label="학기"
            onMove={(direction) => moveSemester.mutate(direction)}
          />
          <Button variant="ghost" className="px-2" disabled={locked} onClick={() => setEditing(true)} title="학기 수정" aria-label="학기 수정">
            <Pencil className="size-4" />
          </Button>
          <Button
            variant="ghost"
            className="px-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700"
            disabled={locked || removeSemester.isPending}
            onClick={confirmRemove}
            title="학기 삭제"
            aria-label="학기 삭제"
          >
            <Trash2 className="size-4" />
          </Button>
        </div>
      </div>
      {removeSemester.error && !isConflict(removeSemester.error) && (
        <Notice tone="danger" className="mx-4 mb-3">
          {removeSemester.error.message}
        </Notice>
      )}

      {/* 과목은 학기 안에 보이지만, 저장은 따로 한다(다른 애그리거트). 그래서 과목마다 자기 revision 을 보인다 */}
      <div className="space-y-1.5 px-4 pb-3 pl-10">
        <div className="space-y-1.5 border-l-2 border-slate-200 pl-3">
          {subjects.map((subject) => (
            <div key={subject.id} className="flex items-center justify-between gap-3 rounded-md border border-slate-200 bg-white px-3 py-2">
              <div className="flex items-center gap-2">
                <BookOpen className="size-4 text-slate-400" />
                <LevelTag tone="subject">과목</LevelTag>
                <Link to={`/authoring/${draft.curriculumId}/subjects/${subject.id}`} className="text-sm font-medium text-brand-700 hover:underline">
                  {subject.title}
                </Link>
                <span className="text-xs text-slate-500">단원 {subject.units.length}개</span>
              </div>
              <div className="flex items-center gap-2">
                <Mono className="hidden text-xs text-slate-400 sm:inline">{subject.id}</Mono>
                <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[11px] text-slate-500" title="과목 자신의 낙관적 락 번호. 뼈대와 따로 오른다">
                  rev {subject.revision}
                </span>
              </div>
            </div>
          ))}
          <AddInline
            label="과목 추가"
            placeholder="과목 이름 (예: 수학)"
            variant="ghost"
            disabled={locked}
            pending={createSubject.isPending}
            error={createSubject.error ? createSubject.error.message : null}
            onSubmit={(title) => createSubject.mutate(title)}
          />
        </div>
      </div>
    </div>
  )
}

// ─── 기본 정보: 보기 → 수정 ─────────────────────────────────────

function InfoTab({ draft, locked, handlers }: { draft: SkeletonDraft; locked: boolean; handlers: Handlers }) {
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(draft.title)
  const [description, setDescription] = useState(draft.description ?? '')
  const [sortOrder, setSortOrder] = useState(String(draft.sortOrder))
  const edit = useMutation({
    mutationFn: () => authoringApi.edit(draft.curriculumId, title.trim(), description.trim(), Number(sortOrder), draft.revision),
    ...handlers,
  })

  return (
    <Card>
      <CardHeader
        title="기본 정보"
        action={
          !editing && (
            <Button variant="secondary" disabled={locked} onClick={() => setEditing(true)}>
              <Pencil className="size-4" />
              수정
            </Button>
          )
        }
      />
      {editing ? (
        <form
          className="space-y-4 p-5"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            edit.mutate()
          }}
        >
          <Row label="제목">
            <Input value={title} onChange={(e) => setTitle(e.target.value)} disabled={locked} />
          </Row>
          <Row label="설명">
            <Input value={description} onChange={(e) => setDescription(e.target.value)} disabled={locked} />
          </Row>
          <Row label="정렬 순서">
            <div className="flex items-center gap-2">
              <Input type="number" value={sortOrder} onChange={(e) => setSortOrder(e.target.value)} disabled={locked} className="w-28" />
              <span className="text-xs text-slate-500">작을수록 목록 앞에 섭니다. 사이에 끼우려면 빈 숫자(예: 15)를 쓰세요</span>
            </div>
          </Row>
          {edit.error && !isConflict(edit.error) && <Notice tone="danger">{edit.error.message}</Notice>}
          <div className="flex justify-end gap-2">
            <Button variant="secondary" onClick={() => setEditing(false)}>
              취소
            </Button>
            <Button type="submit" disabled={locked || !title.trim() || sortOrder === '' || edit.isPending}>
              {edit.isPending && <Loader2 className="size-4 animate-spin" />}
              저장
            </Button>
          </div>
        </form>
      ) : (
        <dl className="divide-y divide-slate-100">
          <Row label="커리큘럼 ID">
            <Mono>{draft.curriculumId}</Mono>
          </Row>
          <Row label="제목">{draft.title}</Row>
          <Row label="설명">{draft.description || <span className="text-slate-400">없음</span>}</Row>
          <Row label="정렬 순서">{draft.sortOrder}</Row>
          <Row label="revision">{draft.revision}</Row>
          {/* 누가 언제 고쳤는지는 아직 저장하지 않는다 */}
          <Row label="최근 수정">
            <span className="text-slate-400">—</span>
          </Row>
        </dl>
      )}
    </Card>
  )
}

function Row({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="grid grid-cols-[8rem_1fr] items-center gap-4 px-5 py-3 text-sm">
      <dt className="text-slate-500">{label}</dt>
      <dd className="text-slate-900">{children}</dd>
    </div>
  )
}

// ─── 판 기록 ────────────────────────────────────────────────────

/** 공개할 때마다 판이 하나씩 쌓인다(창고, curriculum_versions). 맨 위가 배포가 읽는 판이다(진열대) */
function VersionsTab({ versions, draft }: { versions?: VersionSummary[]; draft: SkeletonDraft }) {
  if (versions === undefined) {
    return (
      <Card>
        <div className="p-5">
          <Skeleton className="h-24 w-full" />
        </div>
      </Card>
    )
  }
  if (versions.length === 0) {
    return (
      <Card>
        <EmptyState
          title="아직 공개한 판이 없습니다"
          description="오른쪽 위 [공개]를 누르면 그 순간의 커리큘럼이 판 1로 고정됩니다. 배포는 가장 최근 판을 읽습니다."
        />
      </Card>
    )
  }
  return (
    <Card>
      <CardHeader title="판 기록" description={`판 ${versions.length}개. 맨 위가 배포가 읽는 판입니다`} />
      <Table>
        <thead>
          <tr>
            <Th>판</Th>
            <Th>공개한 때</Th>
            <Th>뼈대 revision</Th>
            <Th>과목</Th>
            <Th />
          </tr>
        </thead>
        <tbody>
          {versions.map((v, index) => (
            <tr key={v.version}>
              <Td className="font-medium tabular-nums">판 {v.version}</Td>
              <Td className="text-slate-600">{formatDateTime(v.publishedAt)}</Td>
              <Td className="text-slate-600 tabular-nums">{v.skeletonRevision}</Td>
              <Td className="text-slate-600">{Object.keys(v.subjectRevisions).length}개</Td>
              <Td>
                {index === 0 && (
                  <span className="rounded-full bg-emerald-50 px-2 py-0.5 text-xs font-medium text-emerald-700 ring-1 ring-inset ring-emerald-200">
                    배포가 읽는 판
                  </span>
                )}
              </Td>
            </tr>
          ))}
        </tbody>
      </Table>
      <p className="border-t border-slate-100 px-5 py-3 text-xs text-slate-500">
        지금 초안: 뼈대 revision {draft.revision}. 판의 내용은 고치지 않습니다. 고치려면 초안을 고치고 다시 공개합니다.
      </p>
    </Card>
  )
}
