import clsx from 'clsx'
import { CheckCircle2, ChevronLeft, ChevronRight, Layers, Loader2, Plus, XCircle } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { TRANSFER_STATUSES, type TransferStatus, type TransferSummary } from '../api/types'
import { Button, Card, CopyButton, EmptyState, ErrorState, Mono, ProgressBar, ProgressText, Skeleton, StatusBadge } from '../components/ui'
import { useStatusCounts, useTransfers } from '../hooks/queries'
import { formatDateTime, formatRelative, shortId } from '../lib/format'
import { transferStatusMeta } from '../lib/status'

const PAGE_SIZE = 10

export function TransferListPage() {
  const [params, setParams] = useSearchParams()
  const status = parseStatus(params.get('status'))
  const page = Math.max(0, Number(params.get('page') ?? 0) || 0)

  const transfers = useTransfers({ status, page, size: PAGE_SIZE })
  const counts = useStatusCounts()

  const select = (next: TransferStatus | undefined) => {
    const p = new URLSearchParams()
    if (next) p.set('status', next)
    setParams(p)
  }
  const goTo = (nextPage: number) => {
    const p = new URLSearchParams(params)
    p.set('page', String(nextPage))
    setParams(p)
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">전송 현황</h1>
          <p className="mt-1 text-sm text-slate-500">요청한 이관이 대상 시스템에 어디까지 반영됐는지 확인합니다.</p>
        </div>
        <Link to="/transfers/new">
          <Button>
            <Plus className="size-4" />새 전송
          </Button>
        </Link>
      </div>

      <StatCards counts={counts.data} loading={counts.isPending} />

      <Card>
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 px-4 py-3">
          <StatusTabs value={status} counts={counts.data} onChange={select} />
          {transfers.isFetching && !transfers.isPending && <Loader2 className="size-4 animate-spin text-slate-400" />}
        </div>

        {transfers.isPending ? (
          <TableSkeleton />
        ) : transfers.isError ? (
          <ErrorState error={transfers.error} onRetry={() => void transfers.refetch()} />
        ) : transfers.data.items.length === 0 ? (
          <EmptyState
            title={status ? `${transferStatusMeta(status).label} 상태인 전송이 없습니다` : '아직 전송이 없습니다'}
            description="새 전송을 요청하면 이곳에서 진행 상황을 볼 수 있습니다."
            action={
              <Link to="/transfers/new">
                <Button>
                  <Plus className="size-4" />새 전송
                </Button>
              </Link>
            }
          />
        ) : (
          <>
            <TransferTable items={transfers.data.items} />
            <Pagination
              page={transfers.data.page}
              totalPages={transfers.data.totalPages}
              totalItems={transfers.data.totalItems}
              onChange={goTo}
            />
          </>
        )}
      </Card>
    </div>
  )
}

function parseStatus(value: string | null): TransferStatus | undefined {
  return TRANSFER_STATUSES.includes(value as TransferStatus) ? (value as TransferStatus) : undefined
}

function StatCards({ counts, loading }: { counts?: Record<TransferStatus, number>; loading: boolean }) {
  const total = counts ? Object.values(counts).reduce((a, b) => a + b, 0) : 0
  const active = counts ? counts.PENDING + counts.SENT + counts.PROCESSED : 0
  const cards: Array<{ label: string; value: number; icon: ReactNode; accent: string; hint?: string }> = [
    { label: '전체 전송', value: total, icon: <Layers className="size-4" />, accent: 'bg-slate-100 text-slate-600' },
    {
      label: '진행 중',
      value: active,
      icon: <Loader2 className={clsx('size-4', active > 0 && 'animate-spin')} />,
      accent: 'bg-sky-50 text-sky-600',
    },
    { label: '완료', value: counts?.SUCCESS ?? 0, icon: <CheckCircle2 className="size-4" />, accent: 'bg-emerald-50 text-emerald-600' },
    { label: '실패', value: counts?.FAILED ?? 0, icon: <XCircle className="size-4" />, accent: 'bg-rose-50 text-rose-600' },
  ]
  return (
    <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
      {cards.map((card) => (
        <Card key={card.label} className="p-4">
          <div className="flex items-center justify-between">
            <p className="text-sm text-slate-500">{card.label}</p>
            <span className={clsx('grid size-7 place-items-center rounded-lg', card.accent)}>{card.icon}</span>
          </div>
          {loading ? (
            <Skeleton className="mt-3 h-8 w-16" />
          ) : (
            <p className="mt-2 text-3xl font-semibold tracking-tight tabular-nums">{card.value.toLocaleString()}</p>
          )}
        </Card>
      ))}
    </div>
  )
}

function StatusTabs({ value, counts, onChange }: {
  value?: TransferStatus
  counts?: Record<TransferStatus, number>
  onChange: (status: TransferStatus | undefined) => void
}) {
  const options: Array<{ value?: TransferStatus; label: string; count?: number }> = [
    { label: '전체', count: counts ? Object.values(counts).reduce((a, b) => a + b, 0) : undefined },
    ...TRANSFER_STATUSES.map((s) => ({ value: s, label: transferStatusMeta(s).label, count: counts?.[s] })),
  ]
  return (
    <div className="flex flex-wrap gap-1 rounded-lg bg-slate-100 p-1">
      {options.map((option) => {
        const selected = option.value === value
        return (
          <button
            key={option.label}
            type="button"
            onClick={() => onChange(option.value)}
            className={clsx(
              'inline-flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium transition',
              selected ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800',
            )}
          >
            {option.label}
            {option.count !== undefined && (
              <span className={clsx('rounded px-1 text-xs tabular-nums', selected ? 'bg-slate-100 text-slate-600' : 'text-slate-400')}>
                {option.count}
              </span>
            )}
          </button>
        )
      })}
    </div>
  )
}

function TransferTable({ items }: { items: TransferSummary[] }) {
  const navigate = useNavigate()
  return (
    <div className="overflow-x-auto">
      <table className="w-full min-w-[760px] text-sm">
        <thead>
          <tr className="text-left text-xs font-medium tracking-wide text-slate-500">
            <th className="px-5 py-3 font-medium">원본</th>
            <th className="px-3 py-3 font-medium">상태</th>
            <th className="w-56 px-3 py-3 font-medium">진행률 (청크)</th>
            <th className="px-3 py-3 font-medium">요청자</th>
            <th className="px-5 py-3 text-right font-medium">요청 시각</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {items.map((t) => {
            const meta = transferStatusMeta(t.status)
            return (
              <tr
                key={t.transferId}
                onClick={() => navigate(`/transfers/${t.transferId}`)}
                className="group cursor-pointer transition hover:bg-slate-50/80"
              >
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-2">
                    <Link
                      to={`/transfers/${t.transferId}`}
                      onClick={(e) => e.stopPropagation()}
                      className="font-medium whitespace-nowrap text-slate-900 group-hover:text-brand-700"
                    >
                      {t.sourceId}
                    </Link>
                    {t.sourceType && (
                      <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[11px] font-medium text-slate-500">{t.sourceType}</span>
                    )}
                  </div>
                  <div className="mt-0.5 flex items-center gap-1 text-slate-400">
                    <Mono>{shortId(t.transferId)}</Mono>
                    <span className="opacity-0 transition group-hover:opacity-100">
                      <CopyButton value={t.transferId} label="전송 ID 복사" />
                    </span>
                  </div>
                </td>
                <td className="px-3 py-3.5">
                  <StatusBadge meta={meta} />
                </td>
                <td className="px-3 py-3.5">
                  <ProgressBar
                    progress={t.progress}
                    tone={t.status === 'FAILED' ? 'neutral' : 'success'}
                    active={meta.active}
                  />
                  <div className="mt-1.5 text-xs">
                    <ProgressText progress={t.progress} />
                  </div>
                </td>
                <td className="px-3 py-3.5 text-slate-600">{t.createdBy}</td>
                <td className="px-5 py-3.5 text-right whitespace-nowrap text-slate-500" title={formatDateTime(t.createdAt)}>
                  {formatRelative(t.createdAt)}
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}

function Pagination({ page, totalPages, totalItems, onChange }: {
  page: number
  totalPages: number
  totalItems: number
  onChange: (page: number) => void
}) {
  if (totalPages <= 1) {
    return <div className="border-t border-slate-100 px-5 py-3 text-xs text-slate-500">총 {totalItems}건</div>
  }
  return (
    <div className="flex items-center justify-between border-t border-slate-100 px-5 py-3 text-xs text-slate-500">
      <span>
        총 {totalItems}건 · {page + 1} / {totalPages} 페이지
      </span>
      <div className="flex gap-1">
        <Button variant="secondary" className="px-2 py-1" disabled={page === 0} onClick={() => onChange(page - 1)} aria-label="이전 페이지">
          <ChevronLeft className="size-4" />
        </Button>
        <Button variant="secondary" className="px-2 py-1" disabled={page + 1 >= totalPages} onClick={() => onChange(page + 1)} aria-label="다음 페이지">
          <ChevronRight className="size-4" />
        </Button>
      </div>
    </div>
  )
}

function TableSkeleton() {
  return (
    <div className="divide-y divide-slate-100">
      {Array.from({ length: 6 }, (_, i) => (
        <div key={i} className="flex items-center gap-6 px-5 py-4">
          <div className="flex-1 space-y-2">
            <Skeleton className="h-4 w-48" />
            <Skeleton className="h-3 w-20" />
          </div>
          <Skeleton className="h-6 w-16 rounded-full" />
          <Skeleton className="h-2 w-48" />
          <Skeleton className="h-4 w-20" />
        </div>
      ))}
    </div>
  )
}
