import { keepPreviousData, type UseQueryResult, useMutation, useQuery } from '@tanstack/react-query'
import { Loader2, Plus, Search } from 'lucide-react'
import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authoringApi, type CurriculumListPage as ListPage, isNotBuiltYet } from '../../api/authoring'
import { Button, Card, EmptyState, ErrorState, Field, Input, Mono, Notice, PageHeader, Pagination, SidePanel, Skeleton, Table, Td, Th } from '../../components/ui'
import { PublishBadge } from './PublishBadge'

const PAGE_SIZE = 20

/**
 * 커리큘럼 목록. 저작의 첫 화면이다. 줄을 누르면 상세로 간다.
 * 목록 API 는 아직 없다. 없으면 안내와 함께 ID로 바로 여는 칸을 보여 준다
 */
export function CurriculumListPage() {
  const [keyword, setKeyword] = useState('')
  const [submitted, setSubmitted] = useState('')
  const [page, setPage] = useState(0)
  const [creating, setCreating] = useState(false)

  const list = useQuery({
    queryKey: ['authoring', 'list', submitted, page],
    queryFn: () => authoringApi.list(submitted, page, PAGE_SIZE),
    placeholderData: keepPreviousData,
    retry: false,
  })

  return (
    <div className="space-y-6">
      <PageHeader
        title="커리큘럼"
        description="초안을 고치고 공개합니다. 배포는 공개한 판만 읽습니다."
        action={
          <Button onClick={() => setCreating(true)}>
            <Plus className="size-4" />새 커리큘럼
          </Button>
        }
      />

      <Card>
        <form
          className="flex gap-2 border-b border-slate-100 p-4"
          onSubmit={(e: FormEvent) => {
            e.preventDefault()
            setPage(0)
            setSubmitted(keyword.trim())
          }}
        >
          <div className="relative flex-1">
            <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-slate-400" />
            <Input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="커리큘럼 ID 또는 제목" className="pl-9" />
          </div>
          <Button type="submit" variant="secondary">
            검색
          </Button>
        </form>

        <ListBody list={list} onCreate={() => setCreating(true)} />

        {list.data && list.data.total > PAGE_SIZE && (
          <div className="border-t border-slate-100 p-4">
            <Pagination page={page} totalPages={Math.ceil(list.data.total / PAGE_SIZE)} onChange={setPage} />
          </div>
        )}
      </Card>

      <CreatePanel open={creating} onClose={() => setCreating(false)} />
    </div>
  )
}

function ListBody({ list, onCreate }: { list: UseQueryResult<ListPage, Error>; onCreate: () => void }) {
  if (list.isPending) {
    return (
      <div className="space-y-2 p-4">
        {Array.from({ length: 5 }, (_, i) => (
          <Skeleton key={i} className="h-10 w-full" />
        ))}
      </div>
    )
  }
  if (list.error) {
    if (isNotBuiltYet(list.error)) return <NotBuiltYet />
    return <ErrorState error={list.error} onRetry={() => void list.refetch()} />
  }
  if (list.data.items.length === 0) {
    return (
      <EmptyState
        title="커리큘럼이 없습니다"
        description="새 커리큘럼을 만들어 학년, 학기, 과목을 채워 보세요."
        action={<Button onClick={onCreate}>새 커리큘럼</Button>}
      />
    )
  }
  return (
    <Table>
      <thead>
        <tr>
          <Th>커리큘럼 ID</Th>
          <Th>제목</Th>
          <Th>정렬 순서</Th>
          <Th>학년</Th>
          <Th>공개</Th>
          <Th>최근 수정</Th>
        </tr>
      </thead>
      <tbody>
        {list.data.items.map((item) => (
          <tr key={item.curriculumId} className="hover:bg-slate-50">
            <Td>
              <Link to={`/authoring/${item.curriculumId}`} className="font-medium text-brand-700 hover:underline">
                <Mono>{item.curriculumId}</Mono>
              </Link>
            </Td>
            <Td>
              <Link to={`/authoring/${item.curriculumId}`} className="text-slate-900 hover:underline">
                {item.title}
              </Link>
            </Td>
            <Td className="text-slate-600 tabular-nums">{item.sortOrder}</Td>
            <Td className="text-slate-600">{item.gradeCount}개</Td>
            <Td>
              <PublishBadge />
            </Td>
            {/* 누가 언제 고쳤는지는 아직 저장하지 않는다 */}
            <Td className="text-slate-400">—</Td>
          </tr>
        ))}
      </tbody>
    </Table>
  )
}

/** 목록 API 가 생기기 전까지 보이는 칸. ID를 알면 바로 연다 */
function NotBuiltYet() {
  const navigate = useNavigate()
  const [id, setId] = useState('')
  return (
    <div className="space-y-4 p-5">
      <Notice tone="info" title="목록은 아직 준비 중입니다">
        서버에 목록 API(<code className="font-mono">GET /api/v1/curricula</code>)가 아직 없습니다. 커리큘럼 ID를 알면 바로 열 수 있습니다.
      </Notice>
      <form
        className="flex max-w-sm gap-2"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          if (id.trim()) navigate(`/authoring/${id.trim().toUpperCase()}`)
        }}
      >
        <Input value={id} onChange={(e) => setId(e.target.value)} placeholder="CUR001" aria-label="커리큘럼 ID" />
        <Button type="submit" variant="secondary" disabled={!id.trim()}>
          열기
        </Button>
      </form>
    </div>
  )
}

/** 새 커리큘럼. 만들면 바로 상세로 간다 */
function CreatePanel({ open, onClose }: { open: boolean; onClose: () => void }) {
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const create = useMutation({
    mutationFn: () => authoringApi.create(title.trim(), description.trim()),
    onSuccess: (created) => {
      setTitle('')
      setDescription('')
      onClose()
      navigate(`/authoring/${created.curriculumId}`)
    },
  })

  return (
    <SidePanel
      open={open}
      title="새 커리큘럼"
      onClose={onClose}
      footer={
        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={onClose}>
            취소
          </Button>
          <Button form="create-curriculum" type="submit" disabled={!title.trim() || create.isPending}>
            {create.isPending && <Loader2 className="size-4 animate-spin" />}
            만들기
          </Button>
        </div>
      }
    >
      <form
        id="create-curriculum"
        className="space-y-4"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          create.mutate()
        }}
      >
        <Field label="제목">
          <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="중등 교육 과정" autoFocus />
        </Field>
        <Field label="설명">
          <Input value={description} onChange={(e) => setDescription(e.target.value)} />
        </Field>
        <p className="text-xs text-slate-500">커리큘럼 ID는 시스템이 CUR001 부터 순서대로 매깁니다. 만든 뒤 상세에서 학년과 학기를 채웁니다.</p>
        {create.error && <Notice tone="danger">{create.error.message}</Notice>}
      </form>
    </SidePanel>
  )
}
