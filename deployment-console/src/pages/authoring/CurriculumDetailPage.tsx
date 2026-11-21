import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import clsx from 'clsx'
import { ChevronRight, Loader2, Pencil, Plus, RotateCw, Upload } from 'lucide-react'
import { type FormEvent, type ReactNode, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { isNotFound } from '../../api'
import { authoringApi, isConflict, type Grade, type Semester, type SkeletonDraft } from '../../api/authoring'
import { Button, Card, CardHeader, EmptyState, ErrorState, Input, Mono, Notice, Skeleton } from '../../components/ui'
import { PublishBadge } from './PublishBadge'

const draftKey = (curriculumId: string) => ['authoring', 'draft', curriculumId] as const

type Tab = 'structure' | 'info' | 'versions'
const TABS: { key: Tab; label: string }[] = [
  { key: 'structure', label: '구성' },
  { key: 'info', label: '기본 정보' },
  { key: 'versions', label: '판 기록' },
]

/** 성공하면 다시 읽고, 409 면 충돌로 남긴다. 고치기마다 같은 처리를 쓴다 */
type Handlers = { onSuccess: () => void; onError: (error: Error) => void }

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
  const [conflict, setConflict] = useState<string | null>(null)
  const [tab, setTab] = useState<Tab>('structure')

  const reload = () => {
    setConflict(null)
    void client.invalidateQueries({ queryKey: draftKey(curriculumId) })
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
          <DetailHeader draft={draft.data} />

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
            {tab === 'versions' && <VersionsTab />}
          </div>
        </>
      )}
    </div>
  )
}

function DetailHeader({ draft }: { draft: SkeletonDraft }) {
  return (
    <div className="flex flex-wrap items-start justify-between gap-4">
      <div>
        <div className="flex items-center gap-2">
          <h1 className="text-2xl font-semibold tracking-tight">{draft.title}</h1>
          <PublishBadge />
        </div>
        <p className="mt-1 flex items-center gap-2 text-sm text-slate-500">
          <Mono>{draft.curriculumId}</Mono>
          <span>·</span>
          <span title="낙관적 락 번호. 고칠 때마다 1씩 오르고, 고칠 때 이 값을 함께 보낸다">revision {draft.revision}</span>
        </p>
      </div>
      {/* 공개는 아직 서버에 없다. 자리만 잡아 둔다 */}
      <Button disabled title="공개 기능은 준비 중입니다">
        <Upload className="size-4" />
        공개
      </Button>
    </div>
  )
}

// ─── 구성: 학년 > 학기 > 과목 ──────────────────────────────────

function StructureTab({ draft, locked, handlers }: { draft: SkeletonDraft; locked: boolean; handlers: Handlers }) {
  const addGrade = useMutation({
    mutationFn: (title: string) => authoringApi.addGrade(draft.curriculumId, title, draft.revision),
    ...handlers,
  })

  return (
    <div className="space-y-4">
      {draft.grades.length === 0 && (
        <Card>
          <EmptyState title="아직 학년이 없습니다" description="학년을 추가하고, 학년 안에 학기를, 학기 안에 과목을 만듭니다." />
        </Card>
      )}
      {draft.grades.map((grade) => (
        <GradeCard key={grade.id} draft={draft} grade={grade} locked={locked} handlers={handlers} />
      ))}
      <AddInline
        label="학년 추가"
        placeholder="학년 이름 (예: 1학년)"
        disabled={locked}
        pending={addGrade.isPending}
        error={addGrade.error && !isConflict(addGrade.error) ? addGrade.error.message : null}
        onSubmit={(title) => addGrade.mutate(title)}
      />
    </div>
  )
}

function GradeCard({ draft, grade, locked, handlers }: { draft: SkeletonDraft; grade: Grade; locked: boolean; handlers: Handlers }) {
  const addSemester = useMutation({
    mutationFn: (title: string) => authoringApi.addSemester(draft.curriculumId, grade.id, title, draft.revision),
    ...handlers,
  })

  return (
    <Card>
      <CardHeader
        title={grade.title}
        description={`학기 ${grade.semesters.length}개`}
        action={<Mono className="text-xs text-slate-400">{grade.id}</Mono>}
      />
      <div className="divide-y divide-slate-100">
        {grade.semesters.map((semester) => (
          <SemesterRow key={semester.id} semester={semester} />
        ))}
      </div>
      <div className="p-4">
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
    </Card>
  )
}

/** 학기 한 줄. 과목은 따로 저장하는 초안이라 아직 없다(과목 만들기는 다음 기능) */
function SemesterRow({ semester }: { semester: Semester }) {
  return (
    <div className="flex items-center justify-between gap-3 px-5 py-3">
      <div>
        <p className="text-sm font-medium text-slate-800">{semester.title}</p>
        <p className="text-xs text-slate-500">과목 0개</p>
      </div>
      <div className="flex items-center gap-3">
        <Mono className="hidden text-xs text-slate-400 sm:inline">{semester.id}</Mono>
        <Button variant="secondary" disabled title="과목 만들기는 준비 중입니다">
          <Plus className="size-4" />
          과목
        </Button>
      </div>
    </div>
  )
}

/** "+ 추가" 버튼을 누르면 입력칸이 열린다 */
function AddInline({
  label,
  placeholder,
  variant = 'secondary',
  disabled,
  pending,
  error,
  onSubmit,
}: {
  label: string
  placeholder: string
  variant?: 'secondary' | 'ghost'
  disabled: boolean
  pending: boolean
  error: string | null
  onSubmit: (title: string) => void
}) {
  const [open, setOpen] = useState(false)
  const [title, setTitle] = useState('')

  if (!open) {
    return (
      <Button variant={variant} disabled={disabled} onClick={() => setOpen(true)}>
        <Plus className="size-4" />
        {label}
      </Button>
    )
  }
  return (
    <div className="space-y-2">
      <form
        className="flex max-w-md gap-2"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          if (title.trim()) onSubmit(title.trim())
        }}
      >
        <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder={placeholder} autoFocus disabled={disabled} />
        <Button type="submit" disabled={disabled || !title.trim() || pending}>
          {pending && <Loader2 className="size-4 animate-spin" />}
          추가
        </Button>
        <Button variant="ghost" onClick={() => setOpen(false)}>
          취소
        </Button>
      </form>
      {error && <Notice tone="danger">{error}</Notice>}
    </div>
  )
}

// ─── 기본 정보: 보기 → 수정 ─────────────────────────────────────

function InfoTab({ draft, locked, handlers }: { draft: SkeletonDraft; locked: boolean; handlers: Handlers }) {
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(draft.title)
  const [description, setDescription] = useState(draft.description ?? '')
  const edit = useMutation({
    mutationFn: () => authoringApi.edit(draft.curriculumId, title.trim(), description.trim(), draft.revision),
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
          {edit.error && !isConflict(edit.error) && <Notice tone="danger">{edit.error.message}</Notice>}
          <div className="flex justify-end gap-2">
            <Button variant="secondary" onClick={() => setEditing(false)}>
              취소
            </Button>
            <Button type="submit" disabled={locked || !title.trim() || edit.isPending}>
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

/** 공개할 때마다 판이 하나씩 쌓인다. 공개 기능이 아직 없어서 비어 있다 */
function VersionsTab() {
  return (
    <Card>
      <EmptyState
        title="아직 공개한 판이 없습니다"
        description="공개하면 그 순간의 커리큘럼이 판 1, 판 2 … 로 쌓이고, 배포는 가장 최근 판을 읽습니다. 공개 기능은 준비 중입니다."
      />
    </Card>
  )
}
