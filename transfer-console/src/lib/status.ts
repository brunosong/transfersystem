import type { ChunkStatus, TransferStatus } from '../api/types'

export type Tone = 'neutral' | 'info' | 'success' | 'danger' | 'warning'

export interface StatusMeta {
  label: string
  tone: Tone
  /** 아직 끝나지 않아 계속 바뀌는 상태 */
  active: boolean
}

/** 운영자에게 보여 주는 네 묶음. transfer_log 상태 다섯 개를 묶는다. */
export type StatusGroup = 'waiting' | 'running' | 'done' | 'failed'

export const STATUS_GROUPS: Record<StatusGroup, { label: string; statuses: TransferStatus[] }> = {
  waiting: { label: '대기', statuses: ['PENDING'] },
  running: { label: '진행 중', statuses: ['SENT', 'PROCESSED'] },
  done: { label: '완료', statuses: ['SUCCESS'] },
  failed: { label: '실패', statuses: ['FAILED'] },
}

export const STATUS_GROUP_ORDER: StatusGroup[] = ['waiting', 'running', 'done', 'failed']

export function isStatusGroup(value: string | null): value is StatusGroup {
  return value !== null && value in STATUS_GROUPS
}

const TRANSFER: Record<TransferStatus, StatusMeta> = {
  PENDING: { label: '대기', tone: 'neutral', active: true },
  SENT: { label: '진행 중', tone: 'info', active: true },
  PROCESSED: { label: '진행 중', tone: 'info', active: true },
  SUCCESS: { label: '완료', tone: 'success', active: false },
  FAILED: { label: '실패', tone: 'danger', active: false },
}

const FALLBACK: StatusMeta = { label: '알 수 없음', tone: 'neutral', active: false }

export function transferStatusMeta(status: string): StatusMeta {
  return TRANSFER[status as TransferStatus] ?? { ...FALLBACK, label: status }
}

export function isActiveTransfer(status: string): boolean {
  return transferStatusMeta(status).active
}

/** 상태 색. 배지·점·막대가 같은 표를 쓴다. */
export const TONE_CLASSES: Record<Tone, { badge: string; dot: string; bar: string; soft: string; text: string }> = {
  neutral: { badge: 'bg-slate-100 text-slate-700 ring-slate-200', dot: 'bg-slate-400', bar: 'bg-slate-400', soft: 'bg-slate-50', text: 'text-slate-600' },
  info: { badge: 'bg-sky-50 text-sky-700 ring-sky-200', dot: 'bg-sky-500', bar: 'bg-sky-500', soft: 'bg-sky-50', text: 'text-sky-700' },
  success: { badge: 'bg-emerald-50 text-emerald-700 ring-emerald-200', dot: 'bg-emerald-500', bar: 'bg-emerald-500', soft: 'bg-emerald-50', text: 'text-emerald-700' },
  danger: { badge: 'bg-rose-50 text-rose-700 ring-rose-200', dot: 'bg-rose-500', bar: 'bg-rose-500', soft: 'bg-rose-50', text: 'text-rose-700' },
  warning: { badge: 'bg-amber-50 text-amber-800 ring-amber-200', dot: 'bg-amber-500', bar: 'bg-amber-500', soft: 'bg-amber-50', text: 'text-amber-800' },
}

/** 청크 한 칸의 색 */
export function chunkClass(status: ChunkStatus | string): string {
  switch (status) {
    case 'SUCCESS':
      return 'bg-emerald-500'
    case 'FAILED':
      return 'bg-rose-500'
    default:
      return 'bg-slate-200'
  }
}
