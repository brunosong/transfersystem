import clsx from 'clsx'
import { ArrowRight, Loader2, Search, Send } from 'lucide-react'
import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { isNotFound } from '../api'
import type { CurriculumSummary } from '../api/types'
import { CurriculumTreeView, LevelCountsLine, TargetName, DeploymentStatusBadge } from '../components/domain'
import { Button, Card, EmptyState, ErrorState, Input, Mono, PageHeader, Pagination, Select, Skeleton } from '../components/ui'
import { useCurriculum, useCurriculumDeployments, useCurriculums, useSources, useTargets } from '../hooks/queries'
import { formatShort } from '../lib/format'
import { sortTargets } from '../lib/targets'

const PAGE_SIZE = 12

export function ContentsPage() {
  const [params, setParams] = useSearchParams()
  const sources = useSources()
  const usable = sources.data?.filter((s) => s.enabled && s.sourceType === 'CURRICULUM') ?? []

  const sourceParam = Number(params.get('source'))
  const sourceId = usable.find((s) => s.sourceConfigId === sourceParam)?.sourceConfigId ?? usable[0]?.sourceConfigId ?? null
  const q = params.get('q') ?? ''
  const page = Math.max(0, Number(params.get('page')) || 0)
  const selected = params.get('c')

  const update = (next: Record<string, string | null>) => {
    const p = new URLSearchParams(params)
    for (const [k, v] of Object.entries(next)) {
      if (v === null || v === '') p.delete(k)
      else p.set(k, v)
    }
    setParams(p, { replace: true })
  }

  // 검색어는 입력이 멈추면 반영한다
  const [keyword, setKeyword] = useState(q)
  useEffect(() => {
    const id = setTimeout(() => keyword !== q && update({ q: keyword, page: null }), 250)
    return () => clearTimeout(id)
  }, [keyword])

  const list = useCurriculums(sourceId, { q, page, size: PAGE_SIZE })

  // 처음 들어오면 첫 커리큘럼을 골라 둔다
  useEffect(() => {
    if (!selected && list.data?.items[0]) update({ c: list.data.items[0].curriculumId })
  }, [list.data, selected])

  return (
    <div className="space-y-6">
      <PageHeader title="콘텐츠" description="원본 저장소에 있는 커리큘럼을 보고 배포할 것을 고릅니다." />

      {sources.isSuccess && usable.length === 0 ? (
        <Card>
          <EmptyState
            title="사용할 수 있는 원본 저장소가 없습니다"
            description="커리큘럼을 읽어 올 원본 저장소를 먼저 등록하거나 켜세요."
            action={
              <Link to="/settings/sources">
                <Button variant="secondary">원본 저장소 설정</Button>
              </Link>
            }
          />
        </Card>
      ) : (
        <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,5fr)_minmax(0,7fr)]">
          <Card>
            <div className="flex flex-col gap-2 border-b border-slate-100 p-3 sm:flex-row">
              <Select
                value={sourceId ?? ''}
                onChange={(e) => update({ source: e.target.value, page: null, c: null })}
                className="sm:w-44"
                aria-label="원본 저장소"
              >
                {usable.map((s) => (
                  <option key={s.sourceConfigId} value={s.sourceConfigId}>
                    {s.name}
                  </option>
                ))}
              </Select>
              <div className="relative flex-1">
                <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-slate-400" />
                <Input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="ID 또는 제목" className="pl-9" aria-label="커리큘럼 검색" />
              </div>
            </div>

            {list.isPending ? (
              <div className="space-y-2 p-3">
                {Array.from({ length: 6 }, (_, i) => (
                  <Skeleton key={i} className="h-11" />
                ))}
              </div>
            ) : list.isError ? (
              <ErrorState error={list.error} title="원본을 읽지 못했습니다" onRetry={() => void list.refetch()} />
            ) : list.data.items.length === 0 ? (
              <EmptyState title="찾는 커리큘럼이 없습니다" description={q ? `'${q}' 와 맞는 커리큘럼이 없습니다.` : undefined} />
            ) : (
              <>
                <ul className="divide-y divide-slate-100">
                  {list.data.items.map((c) => (
                    <CurriculumItem key={c.curriculumId} curriculum={c} selected={c.curriculumId === selected} onSelect={() => update({ c: c.curriculumId })} />
                  ))}
                </ul>
                <div className="flex items-center justify-between border-t border-slate-100 px-3 py-2">
                  <span className="text-xs text-slate-500">총 {list.data.totalItems}개</span>
                  <Pagination page={page} totalPages={list.data.totalPages} onChange={(p) => update({ page: String(p) })} />
                </div>
              </>
            )}
          </Card>

          {sourceId !== null && selected ? (
            <CurriculumPanel sourceId={sourceId} curriculumId={selected} />
          ) : (
            <Card>
              <EmptyState title="커리큘럼을 고르세요" />
            </Card>
          )}
        </div>
      )}
    </div>
  )
}

function CurriculumItem({ curriculum: c, selected, onSelect }: { curriculum: CurriculumSummary; selected: boolean; onSelect: () => void }) {
  return (
    <li>
      <button
        type="button"
        onClick={onSelect}
        className={clsx('flex w-full items-center gap-3 px-4 py-2.5 text-left transition', selected ? 'bg-brand-50' : 'hover:bg-slate-50')}
      >
        <div className="min-w-0 flex-1">
          <p className="truncate text-sm">
            <Mono className={clsx('font-medium', selected ? 'text-brand-700' : 'text-slate-900')}>{c.curriculumId}</Mono>
            <span className="ml-2 text-slate-600">{c.title}</span>
          </p>
          <p className="text-xs text-slate-400">과목 {c.subjectCount}</p>
        </div>
        <div className="shrink-0 text-right text-xs">
          {c.lastDeployment ? (
            <>
              <p className={c.deployedTargetCount > 0 ? 'font-medium text-emerald-700' : 'text-slate-400'}>
                {c.deployedTargetCount > 0 ? `✓ ${c.deployedTargetCount}곳` : '성공 없음'}
              </p>
              {c.lastDeployment.status === 'FAILED' ? (
                <p className="text-rose-600">마지막 배포 실패</p>
              ) : c.lastDeployment.status === 'SENT' || c.lastDeployment.status === 'PENDING' ? (
                <p className="text-sky-600">배포 중</p>
              ) : null}
            </>
          ) : (
            <p className="text-slate-400">배포 안 됨</p>
          )}
        </div>
      </button>
    </li>
  )
}

function CurriculumPanel({ sourceId, curriculumId }: { sourceId: number; curriculumId: string }) {
  const tree = useCurriculum(sourceId, curriculumId)
  const deployments = useCurriculumDeployments(curriculumId)
  const targets = useTargets()

  if (tree.isPending) {
    return (
      <Card className="space-y-3 p-5">
        <Skeleton className="h-7 w-2/3" />
        <Skeleton className="h-4 w-1/2" />
        <Skeleton className="h-48" />
      </Card>
    )
  }
  if (tree.isError) {
    return (
      <Card>
        {isNotFound(tree.error) ? (
          <EmptyState title="원본에 없는 커리큘럼입니다" description={curriculumId} />
        ) : (
          <ErrorState error={tree.error} onRetry={() => void tree.refetch()} />
        )}
      </Card>
    )
  }

  const t = tree.data
  return (
    <Card className="lg:sticky lg:top-24">
      <div className="space-y-2 border-b border-slate-100 p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <Mono className="text-slate-500">{t.curriculumId}</Mono>
            <h2 className="text-lg font-semibold">{t.title}</h2>
          </div>
          <Link to={`/deployments/new?source=${sourceId}&curriculum=${encodeURIComponent(t.curriculumId)}`}>
            <Button>
              <Send className="size-4" />
              배포하기
            </Button>
          </Link>
        </div>
        {t.description && <p className="text-sm text-slate-500">{t.description}</p>}
        <LevelCountsLine tree={t} />
      </div>

      <div className="border-b border-slate-100 p-5">
        <h3 className="mb-2 text-sm font-semibold">대상별 배포 현황</h3>
        {deployments.isPending || targets.isPending ? (
          <Skeleton className="h-24" />
        ) : deployments.isError ? (
          <p className="text-sm text-rose-600">{deployments.error.message}</p>
        ) : (
          <ul className="divide-y divide-slate-100 rounded-lg border border-slate-200">
            {sortTargets(targets.data ?? []).map((target) => {
              const d = deployments.data.find((x) => x.targetId === target.targetId)
              return (
                <li key={target.targetId} className="flex items-center gap-3 px-3 py-2 text-sm">
                  <TargetName target={target} className="w-40" />
                  {d?.last ? (
                    <Link to={`/deployments/${d.last.deploymentId}`} className="flex items-center gap-2 hover:underline">
                      <DeploymentStatusBadge status={d.last.status} size="sm" />
                      <span className="text-xs text-slate-500">{formatShort(d.last.at)}</span>
                    </Link>
                  ) : (
                    <span className="text-xs text-slate-400">배포 안 됨</span>
                  )}
                  {d?.lastSucceededAt && (
                    <Link
                      to={`/targets/${target.targetId}/curriculums/${encodeURIComponent(t.curriculumId)}`}
                      className="ml-auto inline-flex items-center gap-1 text-xs font-medium text-brand-700 hover:underline"
                    >
                      대상 데이터 <ArrowRight className="size-3" />
                    </Link>
                  )}
                </li>
              )
            })}
          </ul>
        )}
      </div>

      <div className="p-5">
        <h3 className="mb-2 flex items-center gap-2 text-sm font-semibold">
          구성 {tree.isFetching && <Loader2 className="size-3.5 animate-spin text-slate-400" />}
        </h3>
        <CurriculumTreeView tree={t} />
      </div>
    </Card>
  )
}
