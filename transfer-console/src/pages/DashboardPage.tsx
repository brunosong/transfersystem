import clsx from 'clsx'
import { AlertTriangle, ArrowRight, CheckCircle2, Loader2, Send, XCircle } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import type { StatusCounts, Target, TransferSummary } from '../api/types'
import { TargetName, TransferStatusBadge } from '../components/domain'
import { Button, Card, CardHeader, EmptyState, ErrorState, Mono, ProgressBar, ProgressText, Skeleton } from '../components/ui'
import { useSources, useStatusCounts, useTargets, useTransfers } from '../hooks/queries'
import { formatRelative, startOfDay } from '../lib/format'
import { isActiveTransfer } from '../lib/status'

// 하루 단위 범위는 화면을 여는 동안 바뀌지 않게 한 번만 계산한다
const today = startOfDay(0).toISOString()
const yesterday = startOfDay(1).toISOString()
const weekAgo = startOfDay(7).toISOString()

const total = (c: StatusCounts | undefined) => (c ? Object.values(c).reduce((a, b) => a + b, 0) : 0)

export function DashboardPage() {
  const todayCounts = useStatusCounts({ from: today })
  const yesterdayCounts = useStatusCounts({ from: yesterday, to: today })
  const allCounts = useStatusCounts()
  const recent = useTransfers({ size: 8 })
  const week = useTransfers({ from: weekAgo, size: 200 })
  const targets = useTargets()

  const running = allCounts.data ? allCounts.data.PENDING + allCounts.data.SENT + allCounts.data.PROCESSED : 0
  const needsAttention = week.data ? unresolvedFailures(week.data.items) : []
  const diff = total(todayCounts.data) - total(yesterdayCounts.data)

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">대시보드</h1>
          <p className="mt-1 text-sm text-slate-500">지금 손봐야 할 배포가 있는지 확인합니다.</p>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          to="/deployments?period=today"
          icon={<Send className="size-4" />}
          label="오늘 배포"
          value={todayCounts.data ? total(todayCounts.data) : null}
          sub={yesterdayCounts.data ? (diff === 0 ? '어제와 같음' : `어제보다 ${diff > 0 ? '+' : ''}${diff}`) : undefined}
        />
        <StatCard
          to="/deployments?group=running"
          icon={<Loader2 className={clsx('size-4', running > 0 && 'animate-spin')} />}
          label="진행 중"
          value={allCounts.data ? running : null}
          tone={running > 0 ? 'info' : undefined}
        />
        <StatCard
          to="/deployments?group=failed&period=week"
          icon={<XCircle className="size-4" />}
          label="확인이 필요한 실패"
          value={week.data ? needsAttention.length : null}
          sub="최근 7일, 다시 배포하지 않은 것"
          tone={needsAttention.length > 0 ? 'danger' : undefined}
        />
        <ConnectionCard />
      </div>

      <Card>
        <CardHeader
          title="확인이 필요한 배포"
          description="실패한 뒤 같은 커리큘럼을 같은 대상으로 아직 다시 보내지 않은 배포입니다."
        />
        {week.isPending ? (
          <ListSkeleton rows={2} />
        ) : week.isError ? (
          <ErrorState error={week.error} onRetry={() => void week.refetch()} />
        ) : needsAttention.length === 0 ? (
          <div className="flex items-center gap-2 px-5 py-6 text-sm text-slate-500">
            <CheckCircle2 className="size-4 text-emerald-500" /> 확인이 필요한 배포가 없습니다.
          </div>
        ) : (
          <ul className="divide-y divide-slate-100">
            {needsAttention.map((t) => (
              <TransferRow key={t.transferId} transfer={t} targets={targets.data} />
            ))}
          </ul>
        )}
      </Card>

      <Card>
        <CardHeader
          title="최근 배포"
          action={
            <Link to="/deployments" className="inline-flex items-center gap-1 text-sm font-medium text-brand-700 hover:underline">
              전체 보기 <ArrowRight className="size-3.5" />
            </Link>
          }
        />
        {recent.isPending ? (
          <ListSkeleton rows={5} />
        ) : recent.isError ? (
          <ErrorState error={recent.error} onRetry={() => void recent.refetch()} />
        ) : recent.data.items.length === 0 ? (
          <EmptyState
            title="아직 배포가 없습니다"
            description="콘텐츠에서 커리큘럼을 골라 첫 배포를 해 보세요."
            action={
              <Link to="/contents">
                <Button variant="secondary">콘텐츠 보러 가기</Button>
              </Link>
            }
          />
        ) : (
          <ul className="divide-y divide-slate-100">
            {recent.data.items.map((t) => (
              <TransferRow key={t.transferId} transfer={t} targets={targets.data} />
            ))}
          </ul>
        )}
      </Card>
    </div>
  )
}

/** 커리큘럼·대상 쌍마다 가장 최근 배포만 보고, 그게 실패면 확인이 필요하다 */
function unresolvedFailures(items: TransferSummary[]): TransferSummary[] {
  const latest = new Map<string, TransferSummary>()
  for (const t of items) {
    const key = `${t.sourceId}#${t.targetId}`
    const prev = latest.get(key)
    if (!prev || prev.createdAt < t.createdAt) latest.set(key, t)
  }
  return [...latest.values()].filter((t) => t.status === 'FAILED').sort((a, b) => b.createdAt.localeCompare(a.createdAt))
}

function StatCard({ to, icon, label, value, sub, tone }: { to: string; icon: ReactNode; label: string; value: number | null; sub?: string; tone?: 'info' | 'danger' }) {
  return (
    <Link to={to} className="group">
      <Card className="h-full p-4 transition group-hover:border-slate-300">
        <p className={clsx('flex items-center gap-1.5 text-sm font-medium', tone === 'danger' ? 'text-rose-600' : tone === 'info' ? 'text-sky-600' : 'text-slate-500')}>
          {icon}
          {label}
        </p>
        {value === null ? (
          <Skeleton className="mt-2 h-8 w-12" />
        ) : (
          <p className={clsx('mt-1 text-3xl font-semibold tabular-nums', tone === 'danger' && 'text-rose-600')}>{value}</p>
        )}
        {sub && <p className="mt-0.5 text-xs text-slate-400">{sub}</p>}
      </Card>
    </Link>
  )
}

function ConnectionCard() {
  const sources = useSources()
  const targets = useTargets()
  const sourceOk = sources.data?.filter((s) => s.lastCheck?.ok).length ?? 0
  const targetOk = targets.data?.filter((t) => t.lastCheck?.ok).length ?? 0
  const broken = (sources.data && sourceOk < sources.data.length) || (targets.data && targetOk < targets.data.length)
  return (
    <Card className="p-4">
      <p className={clsx('flex items-center gap-1.5 text-sm font-medium', broken ? 'text-amber-700' : 'text-slate-500')}>
        {broken ? <AlertTriangle className="size-4" /> : <CheckCircle2 className="size-4" />}
        연결 상태
      </p>
      <div className="mt-2 space-y-1.5 text-sm">
        <ConnectionLine to="/settings/sources" label="원본" ok={sourceOk} total={sources.data?.length} />
        <ConnectionLine to="/settings/targets" label="대상" ok={targetOk} total={targets.data?.length} />
      </div>
      <p className="mt-1.5 text-xs text-slate-400">마지막 연결 테스트 기준</p>
    </Card>
  )
}

function ConnectionLine({ to, label, ok, total }: { to: string; label: string; ok: number; total: number | undefined }) {
  return (
    <Link to={to} className="flex items-center justify-between rounded-md px-1 hover:bg-slate-50">
      <span className="text-slate-600">{label}</span>
      {total === undefined ? (
        <Skeleton className="h-4 w-10" />
      ) : (
        <span className="inline-flex items-center gap-1.5 font-medium tabular-nums">
          <span className={clsx('size-2 rounded-full', ok === total ? 'bg-emerald-500' : 'bg-rose-500')} />
          {ok}/{total}
        </span>
      )}
    </Link>
  )
}

export function TransferRow({ transfer: t, targets }: { transfer: TransferSummary; targets: Target[] | undefined }) {
  const active = isActiveTransfer(t.status)
  return (
    <li>
      <Link to={`/deployments/${t.transferId}`} className="grid grid-cols-[auto_1fr_auto] items-center gap-x-4 gap-y-1 px-5 py-3 transition hover:bg-slate-50 md:grid-cols-[6rem_1fr_13rem_9rem_7rem]">
        <TransferStatusBadge status={t.status} size="sm" />
        <div className="min-w-0">
          <p className="truncate text-sm">
            <Mono className="font-medium text-slate-900">{t.sourceId}</Mono>
            <span className="ml-2 text-slate-600">{t.sourceTitle}</span>
          </p>
        </div>
        <TargetName targetId={t.targetId} targets={targets} className="text-sm text-slate-700" />
        <div className="col-span-2 space-y-1 md:col-span-1">
          <ProgressBar progress={t.progress} active={active} tone={active ? 'info' : 'success'} />
          <p className="text-xs"><ProgressText progress={t.progress} /></p>
        </div>
        <div className="text-right text-xs text-slate-500">
          <p className="text-slate-700">{t.createdBy}</p>
          <p>{formatRelative(t.createdAt)}</p>
        </div>
      </Link>
    </li>
  )
}

function ListSkeleton({ rows }: { rows: number }) {
  return (
    <div className="space-y-3 p-5">
      {Array.from({ length: rows }, (_, i) => (
        <Skeleton key={i} className="h-10" />
      ))}
    </div>
  )
}
