import clsx from 'clsx'
import { AlertTriangle, Check, ChevronLeft, ChevronRight, Copy, Inbox, Info, RotateCw, X } from 'lucide-react'
import {
  type ButtonHTMLAttributes,
  type InputHTMLAttributes,
  type ReactNode,
  type SelectHTMLAttributes,
  useEffect,
  useState,
} from 'react'
import type { ConnectionCheck, Progress, TargetEnvironment } from '../api/types'
import { formatRelative, percent } from '../lib/format'
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

export function PageHeader({ title, description, action }: { title: ReactNode; description?: ReactNode; action?: ReactNode }) {
  return (
    <div className="flex flex-wrap items-end justify-between gap-4">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">{title}</h1>
        {description && <p className="mt-1 text-sm text-slate-500">{description}</p>}
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

export function EmptyState({ title, description, action }: { title: string; description?: ReactNode; action?: ReactNode }) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-14 text-center">
      <div className="mb-4 grid size-12 place-items-center rounded-full bg-slate-100 text-slate-400">
        <Inbox className="size-6" />
      </div>
      <p className="font-medium text-slate-900">{title}</p>
      {description && <p className="mt-1 max-w-sm text-sm text-slate-500">{description}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  )
}

export function ErrorState({ error, onRetry, title = '데이터를 불러오지 못했습니다' }: { error: unknown; onRetry?: () => void; title?: string }) {
  const message = error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.'
  return (
    <div className="flex flex-col items-center justify-center px-6 py-14 text-center">
      <div className="mb-4 grid size-12 place-items-center rounded-full bg-rose-50 text-rose-500">
        <AlertTriangle className="size-6" />
      </div>
      <p className="font-medium text-slate-900">{title}</p>
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

type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger'

export function Button({ variant = 'primary', className, ...props }: ButtonHTMLAttributes<HTMLButtonElement> & { variant?: ButtonVariant }) {
  return (
    <button
      type="button"
      {...props}
      className={clsx(
        'inline-flex items-center justify-center gap-2 rounded-lg px-3.5 py-2 text-sm font-medium whitespace-nowrap transition',
        'focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 focus-visible:outline-none',
        'disabled:cursor-not-allowed disabled:opacity-50',
        variant === 'primary' && 'bg-brand-600 text-white shadow-sm hover:bg-brand-700',
        variant === 'secondary' && 'border border-slate-200 bg-white text-slate-700 shadow-sm hover:bg-slate-50',
        variant === 'ghost' && 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
        variant === 'danger' && 'border border-rose-200 bg-white text-rose-700 shadow-sm hover:bg-rose-50',
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

type NoticeTone = 'info' | 'warning' | 'danger' | 'success'

const NOTICE_CLASSES: Record<NoticeTone, string> = {
  info: 'border-sky-200 bg-sky-50 text-sky-900',
  warning: 'border-amber-200 bg-amber-50 text-amber-900',
  danger: 'border-rose-200 bg-rose-50 text-rose-900',
  success: 'border-emerald-200 bg-emerald-50 text-emerald-900',
}

export function Notice({ tone = 'info', title, children, className }: { tone?: NoticeTone; title?: ReactNode; children?: ReactNode; className?: string }) {
  const Icon = tone === 'info' ? Info : tone === 'success' ? Check : AlertTriangle
  return (
    <div className={clsx('flex gap-2.5 rounded-lg border px-3.5 py-3 text-sm', NOTICE_CLASSES[tone], className)}>
      <Icon className="mt-0.5 size-4 shrink-0" />
      <div className="min-w-0 space-y-0.5">
        {title && <p className="font-medium">{title}</p>}
        {children && <div className="leading-relaxed opacity-90">{children}</div>}
      </div>
    </div>
  )
}

/** 마지막 연결 테스트 결과를 점과 글로 보여 준다 */
export function ConnectionStatus({ check, compact = false }: { check: ConnectionCheck | null; compact?: boolean }) {
  const [dot, label] = !check ? ['bg-slate-300', '확인 안 함'] : check.ok ? ['bg-emerald-500', '정상'] : ['bg-rose-500', '끊김']
  return (
    <span className="inline-flex flex-col" title={check?.message}>
      <span className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-700">
        <span className={clsx('size-2 rounded-full', dot)} />
        {label}
      </span>
      {!compact && check && <span className="text-xs text-slate-400">{formatRelative(check.checkedAt)}</span>}
    </span>
  )
}

export function EnvBadge({ environment }: { environment: TargetEnvironment }) {
  return (
    <span
      className={clsx(
        'rounded px-1.5 py-0.5 text-[11px] font-semibold tracking-wide ring-1 ring-inset',
        environment === 'PROD' ? 'bg-rose-50 text-rose-700 ring-rose-200' : 'bg-slate-100 text-slate-600 ring-slate-200',
      )}
    >
      {environment}
    </span>
  )
}

export function Toggle({ checked, onChange, disabled, label }: { checked: boolean; onChange: (next: boolean) => void; disabled?: boolean; label: string }) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      aria-label={label}
      disabled={disabled}
      onClick={() => onChange(!checked)}
      className={clsx(
        'relative inline-flex h-5 w-9 shrink-0 items-center rounded-full transition disabled:cursor-not-allowed disabled:opacity-50',
        checked ? 'bg-brand-600' : 'bg-slate-300',
      )}
    >
      <span className={clsx('inline-block size-4 rounded-full bg-white shadow transition', checked ? 'translate-x-4.5' : 'translate-x-0.5')} />
    </button>
  )
}

export function Field({ label, hint, error, children, className }: { label: string; hint?: ReactNode; error?: string | null; children: ReactNode; className?: string }) {
  return (
    <label className={clsx('block', className)}>
      <span className="mb-1.5 block text-sm font-medium text-slate-700">{label}</span>
      {children}
      {error ? <span className="mt-1 block text-xs text-rose-600">{error}</span> : hint && <span className="mt-1 block text-xs text-slate-500">{hint}</span>}
    </label>
  )
}

const CONTROL =
  'block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm shadow-sm transition placeholder:text-slate-400 focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 focus:outline-none disabled:bg-slate-50 disabled:text-slate-500'

export function Input({ className, ...props }: InputHTMLAttributes<HTMLInputElement>) {
  return <input {...props} className={clsx(CONTROL, className)} />
}

export function Select({ className, children, ...props }: SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select {...props} className={clsx(CONTROL, 'pr-8', className)}>
      {children}
    </select>
  )
}

export function Pagination({ page, totalPages, onChange }: { page: number; totalPages: number; onChange: (page: number) => void }) {
  if (totalPages <= 1) return null
  return (
    <div className="flex items-center gap-1">
      <Button variant="ghost" className="px-2" disabled={page <= 0} onClick={() => onChange(page - 1)} aria-label="이전 페이지">
        <ChevronLeft className="size-4" />
      </Button>
      <span className="px-2 text-sm text-slate-500 tabular-nums">
        {page + 1} / {totalPages}
      </span>
      <Button variant="ghost" className="px-2" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)} aria-label="다음 페이지">
        <ChevronRight className="size-4" />
      </Button>
    </div>
  )
}

/** 오른쪽에서 열리는 패널. Esc 나 바깥을 누르면 닫힌다. */
export function SidePanel({ open, title, onClose, children, footer }: { open: boolean; title: ReactNode; onClose: () => void; children: ReactNode; footer?: ReactNode }) {
  useEffect(() => {
    if (!open) return
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [open, onClose])

  if (!open) return null
  return (
    <div className="fixed inset-0 z-30">
      <div className="absolute inset-0 bg-slate-900/20 backdrop-blur-[1px]" onClick={onClose} />
      <aside role="dialog" aria-modal="true" className="absolute inset-y-0 right-0 flex w-full max-w-md flex-col bg-white shadow-xl">
        <div className="flex h-16 shrink-0 items-center justify-between border-b border-slate-100 px-5">
          <h2 className="text-base font-semibold">{title}</h2>
          <Button variant="ghost" className="px-2" onClick={onClose} aria-label="닫기">
            <X className="size-4" />
          </Button>
        </div>
        <div className="flex-1 overflow-y-auto p-5">{children}</div>
        {footer && <div className="border-t border-slate-100 px-5 py-4">{footer}</div>}
      </aside>
    </div>
  )
}

export function Table({ children }: { children: ReactNode }) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-left text-sm">{children}</table>
    </div>
  )
}

export function Th({ children, className }: { children?: ReactNode; className?: string }) {
  return <th className={clsx('border-b border-slate-100 px-4 py-2.5 text-xs font-medium whitespace-nowrap text-slate-500', className)}>{children}</th>
}

export function Td({ children, className }: { children?: ReactNode; className?: string }) {
  return <td className={clsx('border-b border-slate-100 px-4 py-3 align-middle', className)}>{children}</td>
}
