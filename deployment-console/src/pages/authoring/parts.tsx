import clsx from 'clsx'
import { Loader2, Pencil, Plus } from 'lucide-react'
import { type FormEvent, type ReactNode, useState } from 'react'
import { Button, Input, Notice } from '../../components/ui'

// 저작 화면(커리큘럼 상세, 과목 상세)이 함께 쓰는 작은 조각들

export const draftKey = (curriculumId: string) => ['authoring', 'draft', curriculumId] as const
// 과목은 뼈대와 다른 애그리거트라 따로 읽고, 따로 새로 고친다
export const subjectsKey = (curriculumId: string) => ['authoring', 'subjects', curriculumId] as const

/** 성공하면 다시 읽고, 409 면 충돌로 남긴다. 고치기마다 같은 처리를 쓴다 */
export type Handlers = { onSuccess: () => void; onError: (error: Error) => void }

/** 학년·학기 같은 단계 이름표. 같은 모양의 카드가 겹쳐도 어느 단계인지 바로 보이게 한다 */
export function LevelTag({ tone, children }: { tone: 'grade' | 'semester' | 'subject' | 'unit'; children: ReactNode }) {
  return (
    <span
      className={clsx(
        'rounded px-1.5 py-0.5 text-[11px] font-semibold ring-1 ring-inset',
        tone === 'grade'
          ? 'bg-brand-50 text-brand-700 ring-brand-200'
          : tone === 'semester'
            ? 'bg-white text-slate-600 ring-slate-200'
            : tone === 'subject'
              ? 'bg-emerald-50 text-emerald-700 ring-emerald-200'
              : 'bg-amber-50 text-amber-800 ring-amber-200',
      )}
    >
      {children}
    </span>
  )
}

/** 아직 서버에 없는 고치기. 자리만 잡아 두고 눌리지 않게 한다 */
export function NotYetButton({ icon: Icon, label }: { icon: typeof Pencil; label: string }) {
  return (
    <Button variant="ghost" className="px-2" disabled title={`${label}는 준비 중입니다`} aria-label={label}>
      <Icon className="size-4" />
    </Button>
  )
}

/** "+ 추가" 버튼을 누르면 입력칸이 열린다 */
export function AddInline({
  label,
  placeholder,
  variant = 'secondary',
  disabled,
  pending,
  error,
  onSubmit,
}: {
  label: string
  placeholder: string
  variant?: 'primary' | 'secondary' | 'ghost'
  disabled: boolean
  pending: boolean
  error: string | null
  onSubmit: (title: string) => void
}) {
  const [open, setOpen] = useState(false)
  const [title, setTitle] = useState('')

  if (!open) {
    return (
      <Button variant={variant} disabled={disabled} onClick={() => setOpen(true)}>
        <Plus className="size-4" />
        {label}
      </Button>
    )
  }
  return (
    <div className="space-y-2">
      <form
        className="flex max-w-md gap-2"
        onSubmit={(e: FormEvent) => {
          e.preventDefault()
          if (title.trim()) onSubmit(title.trim())
        }}
      >
        <Input value={title} onChange={(e) => setTitle(e.target.value)} placeholder={placeholder} autoFocus disabled={disabled} />
        <Button type="submit" disabled={disabled || !title.trim() || pending}>
          {pending && <Loader2 className="size-4 animate-spin" />}
          추가
        </Button>
        <Button variant="ghost" onClick={() => setOpen(false)}>
          취소
        </Button>
      </form>
      {error && <Notice tone="danger">{error}</Notice>}
    </div>
  )
}

