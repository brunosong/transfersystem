import clsx from 'clsx'
import { Loader2, Plus, Search } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import type { StatusCounts, DeploymentQuery } from '../api/types'
import { TargetName, DeploymentStatusBadge } from '../components/domain'
import { Button, Card, EmptyState, ErrorState, Input, Mono, Pagination, ProgressBar, ProgressText, Select, Skeleton, Table, Td, Th } from '../components/ui'
import { useStatusCounts, useTargets, useDeployments } from '../hooks/queries'
import { formatRelative, formatShort, startOfDay } from '../lib/format'
import { isActiveDeployment, isStatusGroup, STATUS_GROUP_ORDER, STATUS_GROUPS, type StatusGroup } from '../lib/status'
import { sortTargets, targetLabel } from '../lib/targets'
import { OPERATORS } from '../lib/user'

const PAGE_SIZE = 15

const PERIODS = {
  today: { label: '오늘', days: 0 },
  week: { label: '최근 7일', days: 7 },
  month: { label: '최근 30일', days: 30 },
  all: { label: '전체 기간', days: null },
} as const
type Period = keyof typeof PERIODS

export function DeploymentsPage() {
  const [params, setParams] = useSearchParams()
  const navigate = useNavigate()
  const targets = useTargets()

  const group = isStatusGroup(params.get('group')) ? (params.get('group') as StatusGroup) : null
  const period: Period = (params.get('period') as Period) in PERIODS ? (params.get('period') as Period) : 'all'
  const targetId = Number(params.get('target')) || undefined
  const createdBy = params.get('user') ?? undefined
  const sourceId = params.get('q') ?? ''
  const page = Math.max(0, Number(params.get('page')) || 0)

  const from = useMemo(() => {
    const days = PERIODS[period].days
    return days === null ? undefined : startOfDay(days).toISOString()
  }, [period])

  const base: DeploymentQuery = { targetId, createdBy, sourceId: sourceId || undefined, from }
  const deployments = useDeployments({ ...base, status: group ? STATUS_GROUPS[group].statuses : undefined, page, size: PAGE_SIZE })
  const counts = useStatusCounts({ from })

  const update = (next: Record<string, string | null>) => {
    const p = new URLSearchParams(params)
    for (const [k, v] of Object.entries(next)) {
      if (v === null || v === '') p.delete(k)
      else p.set(k, v)
    }
    if (!('page' in next)) p.delete('page')
    setParams(p, { replace: true })
  }

  const [keyword, setKeyword] = useState(sourceId)
  useEffect(() => {
    const id = setTimeout(() => keyword !== sourceId && update({ q: keyword }), 250)
    return () => clearTimeout(id)
  }, [keyword])

  const filtered = !!(targetId || createdBy || sourceId || group || period !== 'all')

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">배포 이력</h1>
          <p className="mt-1 text-sm text-slate-500">요청한 배포가 대상 시스템에 어디까지 반영됐는지 확인합니다.</p>
        </div>
        <Link to="/deployments/new">
          <Button>
            <Plus className="size-4" />새 배포
          </Button>
        </Link>
      </div>

      <Card>
        <div className="space-y-3 border-b border-slate-100 p-3">
          <GroupTabs value={group} counts={counts.data} onChange={(g) => update({ group: g })} />
          <div className="flex flex-wrap items-center gap-2">
            <div className="relative w-full sm:w-56">
              <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-slate-400" />
              <Input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="커리큘럼 ID" className="pl-9" aria-label="커리큘럼 ID 검색" />
            </div>
            <Select value={targetId ?? ''} onChange={(e) => update({ target: e.target.value })} className="w-auto" aria-label="대상">
              <option value="">모든 대상</option>
              {sortTargets(targets.data ?? []).map((t) => (
                <option key={t.targetId} value={t.targetId}>
                  {targetLabel(t)}
                </option>
              ))}
            </Select>
            <Select value={period} onChange={(e) => update({ period: e.target.value === 'all' ? null : e.target.value })} className="w-auto" aria-label="기간">
              {Object.entries(PERIODS).map(([key, p]) => (
                <option key={key} value={key}>
                  {p.label}
                </option>
              ))}
            </Select>
            <Select value={createdBy ?? ''} onChange={(e) => update({ user: e.target.value })} className="w-auto" aria-label="요청자">
              <option value="">모든 요청자</option>
              {OPERATORS.map((name) => (
                <option key={name}>{name}</option>
              ))}
            </Select>
            {filtered && (
              <Button
                variant="ghost"
                onClick={() => {
                  setKeyword('')
                  setParams(new URLSearchParams(), { replace: true })
                }}
              >
                조건 지우기
              </Button>
            )}
            {deployments.isFetching && !deployments.isPending && <Loader2 className="ml-auto size-4 animate-spin text-slate-400" />}
          </div>
        </div>

        {deployments.isPending ? (
          <div className="space-y-3 p-5">
            {Array.from({ length: 6 }, (_, i) => (
              <Skeleton key={i} className="h-10" />
            ))}
          </div>
        ) : deployments.isError ? (
          <ErrorState error={deployments.error} onRetry={() => void deployments.refetch()} />
        ) : deployments.data.items.length === 0 ? (
          <EmptyState
            title={filtered ? '조건에 맞는 배포가 없습니다' : '아직 배포가 없습니다'}
            action={
              !filtered && (
                <Link to="/contents">
                  <Button variant="secondary">콘텐츠에서 고르기</Button>
                </Link>
              )
            }
          />
        ) : (
          <>
            <Table>
              <thead>
                <tr>
                  <Th>상태</Th>
                  <Th>커리큘럼</Th>
                  <Th>대상</Th>
                  <Th className="w-48">진행</Th>
                  <Th>요청자</Th>
                  <Th>요청 시각</Th>
                </tr>
              </thead>
              <tbody>
                {deployments.data.items.map((t) => {
                  const active = isActiveDeployment(t.status)
                  return (
                    <tr key={t.deploymentId} onClick={() => navigate(`/deployments/${t.deploymentId}`)} className="cursor-pointer transition hover:bg-slate-50">
                      <Td>
                        <DeploymentStatusBadge status={t.status} size="sm" />
                      </Td>
                      <Td>
                        <Link to={`/deployments/${t.deploymentId}`} onClick={(e) => e.stopPropagation()}>
                          <Mono className="font-medium text-slate-900">{t.sourceId}</Mono>
                        </Link>
                        <p className="max-w-56 truncate text-xs text-slate-500">{t.sourceTitle}</p>
                      </Td>
                      <Td>
                        <TargetName targetId={t.targetId} targets={targets.data} />
                      </Td>
                      <Td>
                        <ProgressBar progress={t.progress} active={active} tone={active ? 'info' : 'success'} />
                        <p className="mt-1 text-xs">
                          <ProgressText progress={t.progress} />
                        </p>
                      </Td>
                      <Td className="text-slate-700">{t.createdBy}</Td>
                      <Td>
                        <p className="text-slate-700 tabular-nums">{formatShort(t.createdAt)}</p>
                        <p className="text-xs text-slate-400">{formatRelative(t.createdAt)}</p>
                      </Td>
                    </tr>
                  )
                })}
              </tbody>
            </Table>
            <div className="flex items-center justify-between px-4 py-2">
              <span className="text-xs text-slate-500">총 {deployments.data.totalItems}건</span>
              <Pagination page={page} totalPages={deployments.data.totalPages} onChange={(p) => update({ page: String(p) })} />
            </div>
          </>
        )}
      </Card>
    </div>
  )
}

function GroupTabs({ value, counts, onChange }: { value: StatusGroup | null; counts: StatusCounts | undefined; onChange: (g: StatusGroup | null) => void }) {
  const countOf = (g: StatusGroup | null) => {
    if (!counts) return null
    const statuses = g ? STATUS_GROUPS[g].statuses : (Object.keys(counts) as (keyof StatusCounts)[])
    return statuses.reduce((n, s) => n + counts[s], 0)
  }
  const tabs: (StatusGroup | null)[] = [null, ...STATUS_GROUP_ORDER]
  return (
    <div className="flex flex-wrap gap-1">
      {tabs.map((g) => {
        const n = countOf(g)
        return (
          <button
            key={g ?? 'all'}
            type="button"
            onClick={() => onChange(g)}
            className={clsx(
              'inline-flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-sm font-medium transition',
              value === g ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100',
            )}
          >
            {g ? STATUS_GROUPS[g].label : '전체'}
            {n !== null && <span className={clsx('tabular-nums', value === g ? 'text-slate-300' : 'text-slate-400')}>{n}</span>}
          </button>
        )
      })}
    </div>
  )
}
