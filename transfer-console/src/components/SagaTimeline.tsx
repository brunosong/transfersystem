import clsx from 'clsx'
import { Check, CircleDashed, Clock, Inbox, Loader2, Undo2, X } from 'lucide-react'
import type { ReactNode } from 'react'
import type { Chunk, TransferDetail, TransferStep } from '../api/types'
import { formatDuration, formatTime } from '../lib/format'
import { ACTION_LABELS, chunkClass, stepStatusMeta, TONE_CLASSES, type Tone } from '../lib/status'
import { targetLabel } from '../lib/targets'
import { ProgressBar, ProgressText, StatusBadge } from './ui'

/**
 * 사가 진행을 세로 타임라인으로 그린다.
 * 접수 → 단계들(반영/되돌리기) → 결과 순서다. 단계 목록은 백엔드가 내려 준 그대로 쓴다.
 */
export function SagaTimeline({ transfer }: { transfer: TransferDetail }) {
  const chunkTotal = transfer.steps.find((s) => s.action === 'APPLY')?.progress.total ?? 0
  return (
    <ol className="relative">
      <TimelineItem
        icon={<Inbox className="size-4" />}
        tone="neutral"
        done
        title="전송 접수"
        time={formatTime(transfer.createdAt)}
      >
        <p className="text-sm text-slate-500">
          원본 데이터를 {chunkTotal > 0 ? `청크 ${chunkTotal}개로` : '청크로'} 나눠 아웃박스에 저장했습니다.
        </p>
      </TimelineItem>

      {transfer.steps.map((step) => (
        <StepItem key={`${step.order}-${step.action}-${step.targetId}`} step={step} />
      ))}

      <OutcomeItem transfer={transfer} />
    </ol>
  )
}

function StepItem({ step }: { step: TransferStep }) {
  const meta = stepStatusMeta(step.status)
  const compensate = step.action === 'COMPENSATE'
  const icon = {
    PENDING: <Clock className="size-4" />,
    PROCESSING: <Loader2 className="size-4 animate-spin" />,
    SUCCEEDED: <Check className="size-4" strokeWidth={3} />,
    FAILED: <X className="size-4" strokeWidth={3} />,
    COMPENSATING: <Undo2 className="size-4 animate-pulse" />,
    COMPENSATED: <Undo2 className="size-4" />,
  }[step.status] ?? <CircleDashed className="size-4" />

  const times = step.chunks.map((c) => c.processedAt).filter(Boolean) as string[]
  const first = times[0]
  const last = times[times.length - 1]

  return (
    <TimelineItem
      icon={icon}
      tone={meta.tone}
      done={step.status !== 'PENDING'}
      title={
        <span className="flex flex-wrap items-center gap-2">
          <span>{targetLabel(step.targetId, step.targetSystem)}</span>
          <span
            className={clsx(
              'rounded px-1.5 py-0.5 text-[11px] font-semibold',
              compensate ? 'bg-amber-100 text-amber-800' : 'bg-brand-50 text-brand-700',
            )}
          >
            {ACTION_LABELS[step.action] ?? step.action}
          </span>
          <StatusBadge meta={meta} size="sm" />
        </span>
      }
      time={first ? `${formatTime(first)}${last && last !== first ? ` · ${formatDuration(first, last)}` : ''}` : undefined}
    >
      {step.status === 'PENDING' ? (
        <p className="text-sm text-slate-400">앞 단계가 끝나면 시작합니다.</p>
      ) : (
        <div className="space-y-3">
          <div className="flex items-center gap-3">
            <ProgressBar
              progress={step.progress}
              tone={compensate ? 'violet' : step.status === 'FAILED' ? 'danger' : 'success'}
              active={meta.active}
              className="flex-1"
            />
            <span className="w-28 text-right text-xs">
              <ProgressText progress={step.progress} />
            </span>
          </div>
          <ChunkGrid
            chunks={step.chunks}
            total={step.progress.total}
            active={meta.active}
            skipped={step.status === 'FAILED'}
          />
        </div>
      )}
    </TimelineItem>
  )
}

function OutcomeItem({ transfer }: { transfer: TransferDetail }) {
  const compensations = transfer.steps.filter((s) => s.action === 'COMPENSATE')
  const compensated = compensations.length > 0 && compensations.every((s) => s.status === 'COMPENSATED')
  const compensating = compensations.some((s) => s.status !== 'COMPENSATED')

  let tone: Tone = 'neutral'
  let icon: ReactNode = <CircleDashed className="size-4" />
  let title = '모든 단계가 끝나면 결과가 표시됩니다'
  let done = false
  if (transfer.status === 'SUCCESS') {
    tone = 'success'
    icon = <Check className="size-4" strokeWidth={3} />
    title = '모든 대상에 반영 완료'
    done = true
  } else if (transfer.status === 'FAILED' && compensated) {
    tone = 'violet'
    icon = <Undo2 className="size-4" />
    title = '보상 완료 · 모든 대상이 이전 상태로 돌아갔습니다'
    done = true
  } else if (transfer.status === 'FAILED' && compensating) {
    tone = 'warning'
    icon = <Undo2 className="size-4 animate-pulse" />
    title = '실패한 전송을 되돌리는 중입니다'
  } else if (transfer.status === 'FAILED') {
    tone = 'danger'
    icon = <X className="size-4" strokeWidth={3} />
    title = '전송 실패'
    done = true
  }

  return (
    <TimelineItem icon={icon} tone={tone} done={done} title={title} last time={done ? formatTime(transfer.updatedAt) : undefined} />
  )
}

function TimelineItem({ icon, tone, done, title, time, last, children }: {
  icon: ReactNode
  tone: Tone
  done: boolean
  title: ReactNode
  time?: string
  last?: boolean
  children?: ReactNode
}) {
  const colors = TONE_CLASSES[tone]
  return (
    <li className="relative flex gap-4 pb-7 last:pb-0">
      {!last && <span aria-hidden className="absolute top-9 bottom-1 left-[17px] w-px bg-slate-200" />}
      <span
        className={clsx(
          'relative z-[1] grid size-9 shrink-0 place-items-center rounded-full ring-4 ring-white',
          done || tone !== 'neutral' ? [colors.soft, colors.text] : 'border border-dashed border-slate-300 bg-white text-slate-400',
        )}
      >
        {icon}
      </span>
      <div className="min-w-0 flex-1 pt-1.5">
        <div className="flex flex-wrap items-baseline justify-between gap-x-4 gap-y-1">
          <div className={clsx('text-sm font-semibold', done || tone !== 'neutral' ? 'text-slate-900' : 'text-slate-400')}>{title}</div>
          {time && <span className="text-xs whitespace-nowrap text-slate-400 tabular-nums">{time}</span>}
        </div>
        {children && <div className="mt-2.5">{children}</div>}
      </div>
    </li>
  )
}

/**
 * 청크 하나를 한 칸으로 그린다. 아직 결과가 없는 칸은 회색이고, 다음 칸은 깜빡인다.
 * 단계가 실패로 끝나 앞으로도 실행되지 않을 칸(skipped)은 점선으로 비워 둔다.
 */
export function ChunkGrid({ chunks, total, active, skipped = false }: {
  chunks: Chunk[]
  total: number
  active: boolean
  skipped?: boolean
}) {
  const sorted = [...chunks].sort((a, b) => a.chunkOffset - b.chunkOffset)
  const pending = Math.max(0, total - sorted.length)
  if (sorted.length === 0 && pending === 0) return null
  return (
    <div className="flex flex-wrap gap-1">
      {sorted.map((chunk) => (
        <span
          key={chunk.chunkOffset}
          title={`청크 ${chunk.chunkOffset} · ${chunk.status}${chunk.processedAt ? ` · ${formatTime(chunk.processedAt)}` : ''}`}
          className={clsx('size-3.5 rounded-[3px] transition-colors', chunkClass(chunk.status))}
        />
      ))}
      {Array.from({ length: pending }, (_, i) => (
        <span
          key={`pending-${i}`}
          title={skipped ? '실행 안 됨' : '대기'}
          className={clsx(
            'size-3.5 rounded-[3px]',
            skipped ? 'border border-dashed border-slate-300' : 'bg-slate-200',
            active && i === 0 && 'animate-pulse bg-slate-300',
          )}
        />
      ))}
    </div>
  )
}
