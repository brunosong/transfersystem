import clsx from 'clsx'
import { ArrowLeft, ArrowRight, CheckCircle2, Loader2, RefreshCw, RotateCw, XCircle } from 'lucide-react'
import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { isNotFound } from '../api'
import type { Chunk, TransferDetail } from '../api/types'
import { ChunkGrid, TargetName, TransferStatusBadge } from '../components/domain'
import { Button, Card, CardHeader, CopyButton, EmptyState, ErrorState, Mono, Notice, ProgressBar, Skeleton } from '../components/ui'
import { useRetryTransfer, useSources, useTargets, useTransfer } from '../hooks/queries'
import { formatDateTime, formatDuration, formatTime, shortId } from '../lib/format'
import { isActiveTransfer } from '../lib/status'

export function DeploymentDetailPage() {
  const { transferId = '' } = useParams()
  const transfer = useTransfer(transferId)

  if (transfer.isPending) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-8 w-1/2" />
        <Skeleton className="h-40" />
        <Skeleton className="h-32" />
      </div>
    )
  }
  if (transfer.isError) {
    return (
      <Card>
        {isNotFound(transfer.error) ? (
          <EmptyState
            title="배포를 찾을 수 없습니다"
            description={transferId}
            action={
              <Link to="/deployments">
                <Button variant="secondary">배포 이력으로</Button>
              </Link>
            }
          />
        ) : (
          <ErrorState error={transfer.error} onRetry={() => void transfer.refetch()} />
        )}
      </Card>
    )
  }
  return <Detail transfer={transfer.data} />
}

function Detail({ transfer: t }: { transfer: TransferDetail }) {
  const targets = useTargets()
  const sources = useSources()
  const retry = useRetryTransfer()
  const [selected, setSelected] = useState<number | null>(null)

  const active = isActiveTransfer(t.status)
  const failed = t.chunks.filter((c) => c.status === 'FAILED')
  const pending = t.progress.total - t.progress.succeeded - t.progress.failed
  const source = sources.data?.find((s) => s.sourceConfigId === t.sourceConfigId)
  const selectedChunk = selected === null ? null : t.chunks[selected]
  const redeployHref = `/deployments/new?source=${t.sourceConfigId}&curriculum=${encodeURIComponent(t.sourceId)}&target=${t.targetId}`

  return (
    <div className="space-y-6">
      <div>
        <Link to="/deployments" className="inline-flex items-center gap-1 text-sm text-slate-500 hover:text-slate-800">
          <ArrowLeft className="size-4" /> 배포 이력
        </Link>
        <div className="mt-2 flex flex-wrap items-center gap-x-3 gap-y-2">
          <h1 className="text-2xl font-semibold tracking-tight">
            <Mono className="text-2xl">{t.sourceId}</Mono> <span className="font-medium text-slate-600">{t.sourceTitle}</span>
          </h1>
          <ArrowRight className="size-5 text-slate-400" />
          <TargetName targetId={t.targetId} targets={targets.data} className="text-lg font-medium" />
          <TransferStatusBadge status={t.status} />
        </div>
        <p className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-slate-500">
          <span>
            요청 <span className="text-slate-700">{t.createdBy}</span> · {formatDateTime(t.createdAt)}
          </span>
          <span>원본 {source?.name ?? `#${t.sourceConfigId}`}</span>
          <span>걸린 시간 {active ? '진행 중' : formatDuration(t.createdAt, t.updatedAt)}</span>
          <span className="inline-flex items-center gap-1">
            배포 ID <Mono>{shortId(t.transferId)}</Mono>
            <CopyButton value={t.transferId} label="배포 ID 복사" />
          </span>
        </p>
      </div>

      {t.resultMessage && (
        <Notice tone={t.status === 'SUCCESS' ? 'success' : 'danger'} title={t.status === 'SUCCESS' ? '배포를 마쳤습니다' : '일부 청크가 반영되지 않았습니다'}>
          {t.resultMessage}
        </Notice>
      )}

      <Card>
        <CardHeader
          title="진행"
          description={
            <span className="tabular-nums">
              성공 {t.progress.succeeded} · 실패 {t.progress.failed} · 남음 {pending} / {t.progress.total}
            </span>
          }
          action={active && <Loader2 className="size-4 animate-spin text-sky-500" />}
        />
        <div className="space-y-4 p-5">
          <ProgressBar progress={t.progress} active={active} tone={active ? 'info' : 'success'} className="h-2" />
          <ChunkGrid chunks={t.chunks} selected={selected} onSelect={(o) => setSelected(selected === o ? null : o)} />
          <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs text-slate-500">
            <Legend className="bg-emerald-500" label="성공" />
            <Legend className="bg-rose-500" label="실패" />
            <Legend className="bg-slate-200" label="대기" />
            <Legend className="bg-amber-300" label="재전송 중" />
            <span>테두리 칸(0번)은 뼈대(커리큘럼·학년·학기), 나머지는 과목 하나씩입니다. 칸을 누르면 내용이 보입니다.</span>
          </div>
          {selectedChunk && <ChunkInfo chunk={selectedChunk} />}
        </div>
      </Card>

      {failed.length > 0 && (
        <Card>
          <CardHeader
            title={`실패한 청크 ${failed.length}개`}
            description="대상 시스템이 돌려준 오류입니다. 원인을 해결한 뒤 실패한 청크만 다시 보낼 수 있습니다."
            action={
              <Button onClick={() => retry.mutate(t.transferId)} disabled={active || retry.isPending}>
                {retry.isPending ? <Loader2 className="size-4 animate-spin" /> : <RotateCw className="size-4" />}
                실패 청크 다시 보내기
              </Button>
            }
          />
          {retry.isError && <Notice tone="danger" className="mx-5 mt-4">{retry.error.message}</Notice>}
          <ul className="divide-y divide-slate-100">
            {failed.map((c) => (
              <li key={c.chunkOffset} className="flex flex-wrap items-start gap-x-4 gap-y-1 px-5 py-3 text-sm">
                <Mono className="w-8 text-slate-400">#{c.chunkOffset}</Mono>
                <span className="w-56 font-medium text-slate-900">{c.label}</span>
                <Mono className="min-w-0 flex-1 text-rose-700">{c.errorMessage ?? '오류 메시지 없음'}</Mono>
                <span className="text-xs text-slate-400">
                  {formatTime(c.processedAt)}
                  {c.retryCount > 0 && ` · 재전송 ${c.retryCount}회`}
                </span>
              </li>
            ))}
          </ul>
        </Card>
      )}

      <Card className="flex flex-wrap items-center justify-between gap-3 p-4">
        <p className="text-sm text-slate-600">
          {active ? (
            '배포가 끝나면 대상에 실제로 들어간 데이터를 원본과 비교할 수 있습니다.'
          ) : t.status === 'SUCCESS' ? (
            <span className="inline-flex items-center gap-1.5">
              <CheckCircle2 className="size-4 text-emerald-500" /> 대상에 원본과 똑같이 들어갔는지 확인해 보세요.
            </span>
          ) : (
            <span className="inline-flex items-center gap-1.5">
              <XCircle className="size-4 text-rose-500" /> 실패한 과목은 대상에 없거나 이전 내용으로 남아 있습니다.
            </span>
          )}
        </p>
        <div className="flex gap-2">
          {active ? (
            <Button variant="secondary" disabled>
              <RefreshCw className="size-4" />
              같은 내용으로 다시 배포
            </Button>
          ) : (
            <Link to={redeployHref}>
              <Button variant="secondary">
                <RefreshCw className="size-4" />
                같은 내용으로 다시 배포
              </Button>
            </Link>
          )}
          <Link to={`/targets/${t.targetId}/curriculums/${encodeURIComponent(t.sourceId)}`}>
            <Button variant="secondary">
              대상 데이터 확인 <ArrowRight className="size-4" />
            </Button>
          </Link>
        </div>
      </Card>
    </div>
  )
}

function Legend({ className, label }: { className: string; label: string }) {
  return (
    <span className="inline-flex items-center gap-1.5">
      <span className={clsx('size-2.5 rounded-[3px]', className)} />
      {label}
    </span>
  )
}

function ChunkInfo({ chunk }: { chunk: Chunk }) {
  const status = chunk.status === 'SUCCESS' ? '성공' : chunk.status === 'FAILED' ? '실패' : chunk.retryCount > 0 ? '재전송 중' : '대기'
  return (
    <div className="grid gap-x-6 gap-y-1 rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm sm:grid-cols-[auto_1fr]">
      <span className="text-slate-500">청크</span>
      <span>
        <Mono>#{chunk.chunkOffset}</Mono> {chunk.label}
      </span>
      <span className="text-slate-500">상태</span>
      <span className={clsx(chunk.status === 'FAILED' && 'text-rose-700', chunk.status === 'SUCCESS' && 'text-emerald-700')}>{status}</span>
      <span className="text-slate-500">처리 시각</span>
      <span>{formatTime(chunk.processedAt)}</span>
      {chunk.retryCount > 0 && (
        <>
          <span className="text-slate-500">재전송</span>
          <span>{chunk.retryCount}회</span>
        </>
      )}
      {chunk.errorMessage && (
        <>
          <span className="text-slate-500">오류</span>
          <Mono className="text-rose-700">{chunk.errorMessage}</Mono>
        </>
      )}
    </div>
  )
}
