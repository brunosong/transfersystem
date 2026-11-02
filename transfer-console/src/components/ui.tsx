import clsx from 'clsx'
import { AlertTriangle, Check, Copy, Inbox, RotateCw } from 'lucide-react'
import { type ButtonHTMLAttributes, type ReactNode, useState } from 'react'
import type { Progress } from '../api/types'
import { percent } from '../lib/format'
import { type StatusMeta, TONE_CLASSES, type Tone } from '../lib/status'

export function Card({ className, children }: { className?: string; children: ReactNode }) {
  return (
    <div className={clsx('rounded-xl border border-slate-200/80 bg-white shadow-[0_1px_2px_rgb(15_23_42/0.04)]', className)}>
      {children}
    </div>
  )
}

export function CardHeader({ title, description, action }: { title: ReactNode; description?: ReactNode; action?: ReactNode }) {
  return (
    <div className="flex items-start justify-between gap-4 border-b border-slate-100 px-5 py-4">
      <div>
        <h2 className="text-[15px] font-semibold text-slate-900">{title}</h2>
        {description && <p className="mt-0.5 text-sm text-slate-500">{description}</p>}
      </div>
      {action}
    </div>
  )
}

export function StatusBadge({ meta, size = 'md' }: { meta: StatusMeta; size?: 'sm' | 'md' }) {
  const tone = TONE_CLASSES[meta.tone]
  return (
    <span
      className={clsx(
        'inline-flex items-center gap-1.5 rounded-full font-medium whitespace-nowrap ring-1 ring-inset',
        tone.badge,
        size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-2.5 py-1 text-xs',
      )}
    >
      <span className="relative flex size-1.5">
        {meta.active && <span className={clsx('absolute inline-flex size-full animate-ping rounded-full opacity-75', tone.dot)} />}
        <span className={clsx('relative inline-flex size-1.5 rounded-full', tone.dot)} />
      </span>
      {meta.label}
    </span>
  )
}

/** 성공·실패를 한 막대에 나눠 그린다. 총 개수를 모르면 회색 막대만 보인다. */
export function ProgressBar({ progress, tone = 'success', active = false, className }: {
  progress: Progress
  tone?: Tone
  active?: boolean
  className?: string
}) {
  const total = progress.total
  const ok = total > 0 ? (progress.succeeded / total) * 100 : 0
  const bad = total > 0 ? (progress.failed / total) * 100 : 0
  return (
    <div className={clsx('flex h-1.5 w-full overflow-hidden rounded-full bg-slate-100', className)}>
      <div
        className={clsx('h-full transition-[width] duration-500 ease-out', TONE_CLASSES[tone].bar, active && 'progress-stripes')}
        style={{ width: `${ok}%` }}
      />
      <div className="h-full bg-rose-500 transition-[width] duration-500 ease-out" style={{ width: `${bad}%` }} />
    </div>
  )
}

export function ProgressText({ progress }: { progress: Progress }) {
  const value = percent(progress)
  if (value === null) return <span className="text-slate-400">—</span>
  return (
    <span className="tabular-nums">
      <span className="font-medium text-slate-900">{progress.succeeded}</span>
      <span className="text-slate-400"> / {progress.total}</span>
      {progress.failed > 0 && <span className="ml-1.5 text-rose-600">실패 {progress.failed}</span>}
    </span>
  )
}

export function Skeleton({ className }: { className?: string }) {
  return <div className={clsx('skeleton', className)} />
}

export function EmptyState({ title, description, action }: { title: string; description?: string; action?: ReactNode }) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <div className="mb-4 grid size-12 place-items-center rounded-full bg-slate-100 text-slate-400">
        <Inbox className="size-6" />
      </div>
      <p className="font-medium text-slate-900">{title}</p>
      {description && <p className="mt-1 max-w-sm text-sm text-slate-500">{description}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  )
}

export function ErrorState({ error, onRetry }: { error: unknown; onRetry?: () => void }) {
  const message = error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.'
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <div className="mb-4 grid size-12 place-items-center rounded-full bg-rose-50 text-rose-500">
        <AlertTriangle className="size-6" />
      </div>
      <p className="font-medium text-slate-900">데이터를 불러오지 못했습니다</p>
      <p className="mt-1 max-w-md text-sm text-slate-500">{message}</p>
      {onRetry && (
        <Button variant="secondary" className="mt-5" onClick={onRetry}>
          <RotateCw className="size-4" />
          다시 시도
        </Button>
      )}
    </div>
  )
}

type ButtonVariant = 'primary' | 'secondary' | 'ghost'

export function Button({ variant = 'primary', className, ...props }: ButtonHTMLAttributes<HTMLButtonElement> & { variant?: ButtonVariant }) {
  return (
    <button
      {...props}
      className={clsx(
        'inline-flex items-center justify-center gap-2 rounded-lg px-3.5 py-2 text-sm font-medium transition',
        'focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 focus-visible:outline-none',
        'disabled:cursor-not-allowed disabled:opacity-50',
        variant === 'primary' && 'bg-brand-600 text-white shadow-sm hover:bg-brand-700',
        variant === 'secondary' && 'border border-slate-200 bg-white text-slate-700 shadow-sm hover:bg-slate-50',
        variant === 'ghost' && 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
        className,
      )}
    />
  )
}

export function CopyButton({ value, label = '복사' }: { value: string; label?: string }) {
  const [copied, setCopied] = useState(false)
  return (
    <button
      type="button"
      title={label}
      aria-label={label}
      onClick={(e) => {
        e.preventDefault()
        e.stopPropagation()
        void navigator.clipboard?.writeText(value).then(() => {
          setCopied(true)
          setTimeout(() => setCopied(false), 1200)
        })
      }}
      className="inline-grid size-6 place-items-center rounded-md text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
    >
      {copied ? <Check className="size-3.5 text-emerald-600" /> : <Copy className="size-3.5" />}
    </button>
  )
}

export function Mono({ children, className }: { children: ReactNode; className?: string }) {
  return <span className={clsx('font-mono text-[12.5px] tracking-tight', className)}>{children}</span>
}
