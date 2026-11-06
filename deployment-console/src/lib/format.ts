import type { Progress } from '../api/types'

const dateTime = new Intl.DateTimeFormat('ko-KR', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit',
  hour12: false,
})

const time = new Intl.DateTimeFormat('ko-KR', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })

/** 백엔드의 LocalDateTime 은 시간대가 없으므로 브라우저 현지 시각으로 읽는다. */
export function toDate(value: string | null | undefined): Date | null {
  if (!value) return null
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}

export function formatDateTime(value: string | null | undefined): string {
  const date = toDate(value)
  return date ? dateTime.format(date) : '—'
}

export function formatTime(value: string | null | undefined): string {
  const date = toDate(value)
  return date ? time.format(date) : '—'
}

export function formatRelative(value: string | null | undefined, now = Date.now()): string {
  const date = toDate(value)
  if (!date) return '—'
  const seconds = Math.round((now - date.getTime()) / 1000)
  if (seconds < 5) return '방금'
  if (seconds < 60) return `${seconds}초 전`
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}분 전`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}시간 전`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}일 전`
  return formatDateTime(value)
}

export function formatDuration(from: string | null | undefined, to: string | null | undefined): string {
  const start = toDate(from)
  const end = toDate(to)
  if (!start || !end) return '—'
  const ms = Math.max(0, end.getTime() - start.getTime())
  if (ms < 1000) return `${ms}ms`
  const seconds = ms / 1000
  if (seconds < 60) return `${seconds.toFixed(1)}초`
  const minutes = Math.floor(seconds / 60)
  return `${minutes}분 ${Math.round(seconds % 60)}초`
}

/** 진행률(0~100). 총 청크 수를 모르면 null */
export function percent(progress: Progress): number | null {
  if (progress.total <= 0) return null
  return Math.min(100, Math.round(((progress.succeeded + progress.failed) / progress.total) * 100))
}

export function shortId(id: string): string {
  return id.slice(0, 8)
}

const number = new Intl.NumberFormat('ko-KR')

export function formatNumber(value: number): string {
  return number.format(value)
}

const shortDateTime = new Intl.DateTimeFormat('ko-KR', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false })

/** 11. 03. 10:42 처럼 짧게 */
export function formatShort(value: string | null | undefined): string {
  const date = toDate(value)
  return date ? shortDateTime.format(date) : '—'
}

/** 오늘 0시부터 days 일 전 0시 (0 이면 오늘 0시) */
export function startOfDay(daysAgo = 0, now = new Date()): Date {
  const date = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  date.setDate(date.getDate() - daysAgo)
  return date
}
