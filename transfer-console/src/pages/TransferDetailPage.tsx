import clsx from 'clsx'
import { AlertTriangle, ArrowLeft, Database, Undo2 } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ApiError } from '../api'
import type { TransferDetail } from '../api/types'
import { SagaTimeline } from '../components/SagaTimeline'
import { Button, Card, CardHeader, CopyButton, EmptyState, ErrorState, Mono, Skeleton, StatusBadge } from '../components/ui'
import { useSourceConfigs, useTransfer } from '../hooks/queries'
import { formatDateTime, formatDuration, formatRelative, percent } from '../lib/format'
import { transferStatusMeta } from '../lib/status'

export function TransferDetailPage() {
  const { transferId = '' } = useParams()
  const transfer = useTransfer(transferId)

  return (
    <div className="space-y-6">
      <Link to="/transfers" className="inline-flex items-center gap-1.5 text-sm text-slate-500 transition hover:text-slate-900">
        <ArrowLeft className="size-4" />
        전송 현황
      </Link>

      {transfer.isPending ? (
        <DetailSkeleton />
      ) : transfer.isError ? (
        transfer.error instanceof ApiError && transfer.error.status === 404 ? (
          <Card>
            <EmptyState
              title="전송을 찾을 수 없습니다"
              description={`ID ${transferId} 에 해당하는 전송이 없습니다.`}
              action={
                <Link to="/transfers">
                  <Button variant="secondary">목록으로</Button>
                </Link>
              }
            />
          </Card>
        ) : (
          <Card>
            <ErrorState error={transfer.error} onRetry={() => void transfer.refetch()} />
          </Card>
        )
      ) : (
        <Detail transfer={transfer.data} />
      )}
    </div>
  )
}

function Detail({ transfer }: { transfer: TransferDetail }) {
  const meta = transferStatusMeta(transfer.status)
  const compensations = transfer.steps.filter((s) => s.action === 'COMPENSATE')
  const compensated = compensations.length > 0 && compensations.every((s) => s.status === 'COMPENSATED')

  return (
    <>
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="truncate text-2xl font-semibold tracking-tight">{transfer.sourceId}</h1>
            <StatusBadge meta={meta} />
          </div>
          <div className="mt-1.5 flex items-center gap-1 text-sm text-slate-500">
            <span>전송 ID</span>
            <Mono className="text-slate-700">{transfer.transferId}</Mono>
            <CopyButton value={transfer.transferId} label="전송 ID 복사" />
          </div>
        </div>
      </div>

      {transfer.resultMessage && (
        <div
          className={clsx(
            'flex gap-3 rounded-xl border p-4 text-sm',
            compensated ? 'border-violet-200 bg-violet-50 text-violet-900' : 'border-rose-200 bg-rose-50 text-rose-900',
          )}
        >
          {compensated ? <Undo2 className="mt-0.5 size-4 shrink-0" /> : <AlertTriangle className="mt-0.5 size-4 shrink-0" />}
          <p className="leading-relaxed">{transfer.resultMessage}</p>
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader
            title="사가 진행"
            description="대상 시스템별로 청크가 반영된 과정입니다. 한 곳이라도 실패하면 반영한 데이터를 거꾸로 되돌립니다."
          />
          <div className="px-5 py-6">
            <SagaTimeline transfer={transfer} />
          </div>
          <Legend />
        </Card>

        <div className="space-y-6">
          <SummaryCard transfer={transfer} />
          <SourceCard sourceConfigId={transfer.sourceConfigId} sourceType={transfer.sourceType} />
        </div>
      </div>
    </>
  )
}

function SummaryCard({ transfer }: { transfer: TransferDetail }) {
  const value = percent(transfer.progress)
  const meta = transferStatusMeta(transfer.status)
  const compensations = transfer.steps.filter((s) => s.action === 'COMPENSATE')
  const compensated = compensations.length > 0 && compensations.every((s) => s.status === 'COMPENSATED')
  const ringTone = compensated ? 'violet' : transfer.status === 'FAILED' ? 'slate' : 'emerald'
  return (
    <Card>
      <CardHeader title="요약" />
      <div className="flex items-center gap-5 px-5 py-5">
        <ProgressRing value={value} tone={ringTone} active={meta.active} />
        <div className="space-y-1 text-sm">
          <p className="text-slate-500">{compensated ? '반영했다가 되돌린 청크' : '반영된 청크'}</p>
          <p className="text-xl font-semibold tabular-nums">
            {transfer.progress.succeeded}
            <span className="text-base font-normal text-slate-400"> / {transfer.progress.total || '—'}</span>
          </p>
          {transfer.progress.failed > 0 && <p className="text-xs text-rose-600">실패 {transfer.progress.failed}개</p>}
        </div>
      </div>
      <dl className="divide-y divide-slate-100 border-t border-slate-100 text-sm">
        <Row label="요청자">{transfer.createdBy}</Row>
        <Row label="요청 시각">
          <span title={formatDateTime(transfer.createdAt)}>{formatRelative(transfer.createdAt)}</span>
        </Row>
        <Row label="마지막 변경">{formatDateTime(transfer.updatedAt)}</Row>
        <Row label="경과 시간">{formatDuration(transfer.createdAt, transfer.updatedAt)}</Row>
      </dl>
    </Card>
  )
}

function SourceCard({ sourceConfigId, sourceType }: { sourceConfigId: number; sourceType: string | null }) {
  const configs = useSourceConfigs()
  const config = configs.data?.find((c) => c.sourceConfigId === sourceConfigId)
  return (
    <Card>
      <CardHeader title="원본" />
      <dl className="divide-y divide-slate-100 text-sm">
        <Row label="유형">{sourceType ?? config?.sourceType ?? '—'}</Row>
        {configs.isPending ? (
          <div className="space-y-2 px-5 py-4">
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-2/3" />
          </div>
        ) : config ? (
          <>
            <Row label="저장소">
              <span className="inline-flex items-center gap-1.5">
                <Database className="size-3.5 text-slate-400" />
                {config.dbType}
              </span>
            </Row>
            <Row label="위치">
              <Mono>
                {config.host}:{config.port}
              </Mono>
            </Row>
            <Row label="DB / 컬렉션">
              <Mono>
                {config.database}.{config.tableOrCollection}
              </Mono>
            </Row>
          </>
        ) : (
          <Row label="설정">#{sourceConfigId}</Row>
        )}
      </dl>
    </Card>
  )
}

function Row({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-4 px-5 py-3">
      <dt className="text-slate-500">{label}</dt>
      <dd className="min-w-0 truncate text-right text-slate-900">{children}</dd>
    </div>
  )
}

const RING_TONES = { emerald: 'stroke-emerald-500', violet: 'stroke-violet-500', slate: 'stroke-slate-400' }

function ProgressRing({ value, tone, active }: { value: number | null; tone: keyof typeof RING_TONES; active: boolean }) {
  const radius = 30
  const circumference = 2 * Math.PI * radius
  const offset = circumference * (1 - (value ?? 0) / 100)
  return (
    <div className="relative size-20 shrink-0">
      <svg viewBox="0 0 72 72" className="size-20 -rotate-90">
        <circle cx="36" cy="36" r={radius} fill="none" strokeWidth="7" className="stroke-slate-100" />
        <circle
          cx="36"
          cy="36"
          r={radius}
          fill="none"
          strokeWidth="7"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          className={clsx(
            'transition-[stroke-dashoffset] duration-700 ease-out',
            RING_TONES[tone],
            active && 'animate-pulse',
          )}
        />
      </svg>
      <span className="absolute inset-0 grid place-items-center text-base font-semibold tabular-nums">
        {value === null ? '—' : `${value}%`}
      </span>
    </div>
  )
}

function Legend() {
  const items = [
    ['bg-emerald-500', '반영됨'],
    ['bg-rose-500', '실패'],
    ['bg-violet-500', '되돌림'],
    ['bg-slate-200', '대기'],
    ['border border-dashed border-slate-300', '실행 안 됨'],
  ]
  return (
    <div className="flex flex-wrap gap-4 border-t border-slate-100 px-5 py-3 text-xs text-slate-500">
      {items.map(([color, label]) => (
        <span key={label} className="inline-flex items-center gap-1.5">
          <span className={clsx('size-2.5 rounded-[3px]', color)} />
          {label}
        </span>
      ))}
    </div>
  )
}

function DetailSkeleton() {
  return (
    <div className="space-y-6">
      <div className="space-y-2">
        <Skeleton className="h-8 w-72" />
        <Skeleton className="h-4 w-96" />
      </div>
      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="space-y-6 p-6 lg:col-span-2">
          {Array.from({ length: 3 }, (_, i) => (
            <div key={i} className="flex gap-4">
              <Skeleton className="size-9 rounded-full" />
              <div className="flex-1 space-y-2">
                <Skeleton className="h-4 w-40" />
                <Skeleton className="h-2 w-full" />
              </div>
            </div>
          ))}
        </Card>
        <Card className="space-y-3 p-6">
          <Skeleton className="h-20 w-20 rounded-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-2/3" />
        </Card>
      </div>
    </div>
  )
}
