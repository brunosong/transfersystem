import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { BookOpen, ChevronRight, FileText, Layers, Loader2, Pencil, RotateCw, Trash2 } from 'lucide-react'
import { type FormEvent, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { authoringApi, isConflict, type Lesson, type MoveDirection, type SkeletonDraft, type SubjectDraft, type Unit } from '../../api/authoring'
import { Button, Card, CardHeader, EmptyState, ErrorState, Input, Mono, Notice, Select, Skeleton } from '../../components/ui'
import { AddInline, draftKey, LevelTag, MoveButtons, subjectsKey } from './parts'

/**
 * 과목 상세. 과목 안의 단원(그리고 다음에 차시)을 고친다.
 *
 * 과목은 뼈대와 다른 애그리거트다. 그래서 이 화면은 과목의 revision 을 들고 있다가 고칠 때 보낸다.
 * 뼈대(학년, 학기)를 누가 고쳐도 여기서는 409 가 나지 않는다. 같은 과목을 누가 먼저 고쳤을 때만 난다.
 *
 * 과목 하나 읽기 API 는 아직 없어서 커리큘럼의 과목 목록에서 찾는다
 */
export function SubjectDetailPage() {
  const { curriculumId = '', subjectId = '' } = useParams()
  const client = useQueryClient()
  // 들고 있는 revision 이 저절로 바뀌지 않게 창을 다시 볼 때 새로 읽지 않는다(커리큘럼 상세와 같다)
  const subjects = useQuery({
    queryKey: subjectsKey(curriculumId),
    queryFn: () => authoringApi.listSubjects(curriculumId),
    refetchOnWindowFocus: false,
    staleTime: Infinity,
    retry: false,
  })
  // 어느 학년·학기의 과목인지 보여 주려고 뼈대도 읽는다. 고치지는 않는다
  const draft = useQuery({
    queryKey: draftKey(curriculumId),
    queryFn: () => authoringApi.get(curriculumId),
    refetchOnWindowFocus: false,
    staleTime: Infinity,
    retry: false,
  })
  const [conflict, setConflict] = useState<string | null>(null)

  const subject = subjects.data?.find((s) => s.id === subjectId)
  const reload = () => {
    setConflict(null)
    void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) })
  }

  return (
    <div className="space-y-6">
      <nav className="flex flex-wrap items-center gap-1 text-sm text-slate-500" aria-label="경로">
        <Link to="/authoring" className="hover:text-slate-800">
          커리큘럼
        </Link>
        <ChevronRight className="size-4" />
        <Link to={`/authoring/${curriculumId}`} className="hover:text-slate-800">
          <Mono>{curriculumId}</Mono>
        </Link>
        <ChevronRight className="size-4" />
        <span className="text-slate-700">{subject?.title ?? '과목'}</span>
      </nav>

      {subjects.isPending ? (
        <Card>
          <div className="space-y-3 p-5">
            <Skeleton className="h-7 w-64" />
            <Skeleton className="h-32 w-full" />
          </div>
        </Card>
      ) : subjects.error ? (
        <Card>
          <ErrorState error={subjects.error} onRetry={reload} />
        </Card>
      ) : !subject ? (
        <Card>
          <EmptyState
            title="과목이 없습니다"
            description={`${curriculumId} 에 이 과목이 없거나 지워졌습니다.`}
            action={
              <Link to={`/authoring/${curriculumId}`}>
                <Button variant="secondary">커리큘럼으로</Button>
              </Link>
            }
          />
        </Card>
      ) : (
        <>
          {/* revision 이 바뀌면 머리의 입력 상태도 처음으로 돌린다 */}
          <SubjectHeader
            key={`header-${subject.revision}`}
            curriculumId={curriculumId}
            subject={subject}
            draft={draft.data}
            locked={conflict !== null}
            onConflict={setConflict}
          />

          {conflict && (
            <Notice tone="warning" title="다른 사람이 먼저 이 과목을 고쳤습니다">
              <p>{conflict}</p>
              <p>방금 고친 내용은 저장되지 않았습니다. 다시 불러와 최신 내용을 본 뒤 고치세요.</p>
              <Button variant="secondary" className="mt-2" onClick={reload}>
                <RotateCw className="size-4" />
                다시 불러오기
              </Button>
            </Notice>
          )}

          {/* revision 이 바뀌면(다시 읽으면) 안쪽 입력 상태를 처음으로 돌린다 */}
          <UnitsSection
            key={subject.revision}
            curriculumId={curriculumId}
            subject={subject}
            locked={conflict !== null}
            onConflict={setConflict}
          />
        </>
      )}
    </div>
  )
}

function SubjectHeader({
  curriculumId,
  subject,
  draft,
  locked,
  onConflict,
}: {
  curriculumId: string
  subject: SubjectDraft
  draft?: SkeletonDraft
  locked: boolean
  onConflict: (message: string) => void
}) {
  const client = useQueryClient()
  // 연필을 누르면 머리가 입력칸으로 바뀐다
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(subject.title)
  const [description, setDescription] = useState(subject.description ?? '')
  const [sortOrder, setSortOrder] = useState(String(subject.sortOrder))
  const navigate = useNavigate()
  // 과목을 지우면 단원과 차시도 함께 사라진다. 지운 뒤에는 커리큘럼 화면으로 돌아간다
  const removeSubject = useMutation({
    mutationFn: () => authoringApi.removeSubject(curriculumId, subject.id, subject.revision),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: subjectsKey(curriculumId) })
      navigate(`/authoring/${curriculumId}`)
    },
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  const confirmRemoveSubject = () => {
    const units = subject.units.length > 0 ? `\n단원 ${subject.units.length}개와 그 안의 차시도 함께 지워집니다.` : ''
    if (window.confirm(`'${subject.title}' 과목을 지웁니다.${units}`)) removeSubject.mutate()
  }
  const editSubject = useMutation({
    mutationFn: () => authoringApi.editSubject(curriculumId, subject.id, title.trim(), description.trim(), Number(sortOrder), subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })

  // 뼈대에서 이 과목의 학년·학기 이름을 찾는다. 뼈대를 못 읽었거나 학기가 지워졌으면 ID만 보인다
  const place = draft?.grades
    .flatMap((g) => g.semesters.map((s) => ({ grade: g.title, semester: s.title, semesterId: s.id })))
    .find((p) => p.semesterId === subject.semesterId)

  if (editing) {
    return (
      <Card>
        <form
          className="space-y-3 p-5"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            editSubject.mutate()
          }}
        >
          <div className="grid gap-3 sm:grid-cols-2">
            <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="과목 이름" aria-label="과목 이름" autoFocus disabled={locked} />
            <Input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" aria-label="과목 설명" disabled={locked} />
          </div>
          <label className="flex items-center gap-2 text-sm text-slate-600">
            정렬 순서
            <Input type="number" value={sortOrder} onChange={(e) => setSortOrder(e.target.value)} aria-label="정렬 순서" disabled={locked} className="w-28" />
            <span className="text-xs text-slate-500">같은 학기 안에서 작을수록 앞에 섭니다</span>
          </label>
          {editSubject.error && !isConflict(editSubject.error) && <Notice tone="danger">{editSubject.error.message}</Notice>}
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setEditing(false)}>
              취소
            </Button>
            <Button type="submit" disabled={locked || !title.trim() || editSubject.isPending}>
              {editSubject.isPending && <Loader2 className="size-4 animate-spin" />}
              저장 (과목 revision {subject.revision} 기준)
            </Button>
          </div>
        </form>
      </Card>
    )
  }

  return (
    <div className="flex flex-wrap items-start justify-between gap-4">
      <div>
        <div className="flex items-center gap-2">
          <span className="grid size-8 place-items-center rounded-lg bg-emerald-50 text-emerald-700">
            <BookOpen className="size-4" />
          </span>
          <LevelTag tone="subject">과목</LevelTag>
          <h1 className="text-2xl font-semibold tracking-tight">{subject.title}</h1>
        </div>
        <p className="mt-1 flex flex-wrap items-center gap-2 text-sm text-slate-500">
          <span>{place ? `${place.grade} · ${place.semester}` : <Mono>{subject.semesterId}</Mono>}</span>
          <span>·</span>
          <Mono>{subject.id}</Mono>
          <span>·</span>
          <span>정렬 순서 {subject.sortOrder}</span>
          <span>·</span>
          <span title="이 과목의 낙관적 락 번호. 뼈대와 따로 오른다">과목 revision {subject.revision}</span>
        </p>
        {subject.description && <p className="mt-1 text-sm text-slate-600">{subject.description}</p>}
      </div>
      {/* 다른 학기로 옮기기는 아직 서버에 없다 */}
      <div className="flex items-center gap-1">
        <Button variant="ghost" className="px-2" disabled={locked} onClick={() => setEditing(true)} title="과목 수정" aria-label="과목 수정">
          <Pencil className="size-4" />
        </Button>
        <Button
          variant="ghost"
          className="px-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700"
          disabled={locked || removeSubject.isPending}
          onClick={confirmRemoveSubject}
          title="과목 삭제"
          aria-label="과목 삭제"
        >
          <Trash2 className="size-4" />
        </Button>
      </div>
      {removeSubject.error && !isConflict(removeSubject.error) && (
        <Notice tone="danger" className="w-full">
          {removeSubject.error.message}
        </Notice>
      )}
    </div>
  )
}

function UnitsSection({
  curriculumId,
  subject,
  locked,
  onConflict,
}: {
  curriculumId: string
  subject: SubjectDraft
  locked: boolean
  onConflict: (message: string) => void
}) {
  const client = useQueryClient()
  const addUnit = useMutation({
    mutationFn: (title: string) => authoringApi.addUnit(curriculumId, subject.id, title, subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <p className="pt-2 text-sm text-slate-500">단원 {subject.units.length}개 · 단원 안에 차시가 들어갑니다</p>
        <AddInline
          label="단원 추가"
          placeholder="단원 이름 (예: 1단원 수와 연산)"
          variant="primary"
          disabled={locked}
          pending={addUnit.isPending}
          error={addUnit.error && !isConflict(addUnit.error) ? addUnit.error.message : null}
          onSubmit={(title) => addUnit.mutate(title)}
        />
      </div>

      {subject.units.length === 0 && (
        <Card>
          <EmptyState title="아직 단원이 없습니다" description="오른쪽 위 [단원 추가]로 첫 단원을 만드세요." />
        </Card>
      )}
      {subject.units.map((unit, index) => (
        <UnitCard
          key={unit.id}
          curriculumId={curriculumId}
          subject={subject}
          unit={unit}
          order={index + 1}
          locked={locked}
          onConflict={onConflict}
        />
      ))}
    </div>
  )
}

/** 단원 카드. 안에 차시가 들어간다. 차시를 붙이면 과목의 revision 이 오른다 */
function UnitCard({
  curriculumId,
  subject,
  unit,
  order,
  locked,
  onConflict,
}: {
  curriculumId: string
  subject: SubjectDraft
  unit: Unit
  order: number
  locked: boolean
  onConflict: (message: string) => void
}) {
  const client = useQueryClient()
  const addLesson = useMutation({
    mutationFn: (title: string) => authoringApi.addLesson(curriculumId, subject.id, unit.id, title, subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  // 연필을 누르면 단원 머리가 입력칸으로 바뀐다
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(unit.title)
  const [description, setDescription] = useState(unit.description ?? '')
  const moveUnit = useMutation({
    mutationFn: (direction: MoveDirection) => authoringApi.moveUnit(curriculumId, subject.id, unit.id, direction, subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  const removeUnit = useMutation({
    mutationFn: () => authoringApi.removeUnit(curriculumId, subject.id, unit.id, subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  const confirmRemoveUnit = () => {
    const lessons = unit.lessons.length > 0 ? `\n차시 ${unit.lessons.length}개도 함께 지워집니다.` : ''
    if (window.confirm(`'${unit.title}' 단원을 지웁니다.${lessons}`)) removeUnit.mutate()
  }
  const editUnit = useMutation({
    mutationFn: () =>
      authoringApi.editUnit(curriculumId, subject.id, unit.id, title.trim(), description.trim(), subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })

  return (
    <Card>
      {editing ? (
        <form
          className="space-y-2 border-b border-slate-100 px-5 py-4"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            editUnit.mutate()
          }}
        >
          <div className="grid gap-2 sm:grid-cols-2">
            <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="단원 이름" aria-label="단원 이름" autoFocus disabled={locked} />
            <Input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" aria-label="단원 설명" disabled={locked} />
          </div>
          {editUnit.error && !isConflict(editUnit.error) && <Notice tone="danger">{editUnit.error.message}</Notice>}
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setEditing(false)}>
              취소
            </Button>
            <Button type="submit" disabled={locked || !title.trim() || editUnit.isPending}>
              {editUnit.isPending && <Loader2 className="size-4 animate-spin" />}
              저장
            </Button>
          </div>
        </form>
      ) : (
        <CardHeader
          title={
            <span className="flex items-center gap-2">
              <span className="grid size-7 place-items-center rounded-lg bg-amber-50 text-amber-700">
                <Layers className="size-4" />
              </span>
              <LevelTag tone="unit">단원 {order}</LevelTag>
              <span>{unit.title}</span>
            </span>
          }
          description={unit.description ? `${unit.description} · 차시 ${unit.lessons.length}개` : `차시 ${unit.lessons.length}개`}
          action={
            <div className="flex items-center gap-1">
              <Mono className="mr-2 hidden text-xs text-slate-400 sm:inline">{unit.id}</Mono>
              <MoveButtons
                index={order - 1}
                count={subject.units.length}
                disabled={locked || moveUnit.isPending}
                label="단원"
                onMove={(direction) => moveUnit.mutate(direction)}
              />
              <Button variant="ghost" className="px-2" disabled={locked} onClick={() => setEditing(true)} title="단원 수정" aria-label="단원 수정">
                <Pencil className="size-4" />
              </Button>
              <Button
                variant="ghost"
                className="px-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700"
                disabled={locked || removeUnit.isPending}
                onClick={confirmRemoveUnit}
                title="단원 삭제"
                aria-label="단원 삭제"
              >
                <Trash2 className="size-4" />
              </Button>
            </div>
          }
        />
      )}
      <div className="py-4 pr-4 pl-8">
        <div className="space-y-1.5 border-l-2 border-amber-100 pl-4">
          {unit.lessons.length === 0 && <p className="py-1 text-sm text-slate-400">아직 차시가 없습니다</p>}
          {unit.lessons.map((lesson, index) => (
            <LessonRow
              key={lesson.id}
              curriculumId={curriculumId}
              subject={subject}
              unit={unit}
              lesson={lesson}
              order={index + 1}
              locked={locked}
              onConflict={onConflict}
            />
          ))}
          <AddInline
            label="차시 추가"
            placeholder="차시 이름 (예: 1차시 자연수의 덧셈)"
            variant="ghost"
            disabled={locked}
            pending={addLesson.isPending}
            error={addLesson.error && !isConflict(addLesson.error) ? addLesson.error.message : null}
            onSubmit={(title) => addLesson.mutate(title)}
          />
        </div>
      </div>
    </Card>
  )
}

/** 차시 한 줄. 연필을 누르면 이름과 설명을 고친다. 차시를 고쳐도 과목의 revision 이 오른다 */
function LessonRow({
  curriculumId,
  subject,
  unit,
  lesson,
  order,
  locked,
  onConflict,
}: {
  curriculumId: string
  subject: SubjectDraft
  unit: Unit
  lesson: Lesson
  order: number
  locked: boolean
  onConflict: (message: string) => void
}) {
  const client = useQueryClient()
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(lesson.title)
  const [description, setDescription] = useState(lesson.description ?? '')
  const [learningLevel, setLearningLevel] = useState(lesson.learningLevel)
  const moveLesson = useMutation({
    mutationFn: (direction: MoveDirection) =>
      authoringApi.moveLesson(curriculumId, subject.id, unit.id, lesson.id, direction, subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  const removeLesson = useMutation({
    mutationFn: () => authoringApi.removeLesson(curriculumId, subject.id, unit.id, lesson.id, subject.revision),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })
  const editLesson = useMutation({
    mutationFn: () =>
      authoringApi.editLesson(
        curriculumId,
        subject.id,
        unit.id,
        lesson.id,
        title.trim(),
        description.trim(),
        learningLevel,
        subject.revision,
      ),
    onSuccess: () => void client.invalidateQueries({ queryKey: subjectsKey(curriculumId) }),
    onError: (error: Error) => {
      if (isConflict(error)) onConflict(error.message)
    },
  })

  if (editing) {
    return (
      <form
        className="space-y-2 rounded-md border border-slate-200 bg-slate-50 px-3 py-2"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          editLesson.mutate()
        }}
      >
        <div className="grid gap-2 sm:grid-cols-2">
          <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="차시 이름" aria-label="차시 이름" autoFocus disabled={locked} />
          <Input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" aria-label="차시 설명" disabled={locked} />
        </div>
        <label className="flex items-center gap-2 text-sm text-slate-600">
          학습 수준
          <Select
            value={learningLevel}
            onChange={(e) => setLearningLevel(Number(e.target.value))}
            aria-label="학습 수준"
            disabled={locked}
            className="w-24"
          >
            {[1, 2, 3, 4, 5].map((level) => (
              <option key={level} value={level}>
                {level}
              </option>
            ))}
          </Select>
        </label>
        {editLesson.error && !isConflict(editLesson.error) && <Notice tone="danger">{editLesson.error.message}</Notice>}
        <div className="flex justify-end gap-2">
          <Button variant="ghost" onClick={() => setEditing(false)}>
            취소
          </Button>
          <Button type="submit" disabled={locked || !title.trim() || editLesson.isPending}>
            {editLesson.isPending && <Loader2 className="size-4 animate-spin" />}
            저장
          </Button>
        </div>
      </form>
    )
  }

  return (
    <div className="flex items-center justify-between gap-3 rounded-md border border-slate-200 bg-slate-50 px-3 py-2">
      <div className="flex items-center gap-2">
        <FileText className="size-4 text-slate-400" />
        <LevelTag tone="lesson">차시 {order}</LevelTag>
        <span className="text-sm text-slate-800">{lesson.title}</span>
        <span className="rounded bg-white px-1.5 py-0.5 text-[11px] text-slate-500 ring-1 ring-inset ring-slate-200" title="학습 수준 1~5">
          수준 {lesson.learningLevel}
        </span>
        {lesson.description && <span className="text-xs text-slate-500">{lesson.description}</span>}
      </div>
      <div className="flex items-center gap-1">
        <Mono className="mr-2 hidden text-xs text-slate-400 sm:inline">{lesson.id}</Mono>
        <MoveButtons
          index={order - 1}
          count={unit.lessons.length}
          disabled={locked || moveLesson.isPending}
          label="차시"
          onMove={(direction) => moveLesson.mutate(direction)}
        />
        <Button variant="ghost" className="px-2" disabled={locked} onClick={() => setEditing(true)} title="차시 수정" aria-label="차시 수정">
          <Pencil className="size-4" />
        </Button>
        <Button
          variant="ghost"
          className="px-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700"
          disabled={locked || removeLesson.isPending}
          onClick={() => window.confirm(`'${lesson.title}' 차시를 지웁니다.`) && removeLesson.mutate()}
          title="차시 삭제"
          aria-label="차시 삭제"
        >
          <Trash2 className="size-4" />
        </Button>
      </div>
    </div>
  )
}
