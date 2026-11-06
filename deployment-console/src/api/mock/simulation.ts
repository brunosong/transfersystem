// 배포 하나가 시간에 따라 어떻게 진행되는지 흉내 낸다.
// 청크마다 "언제 처리되고 결과가 어떤지"를 시도(attempt) 목록으로 미리 정해 두고,
// 화면이 물어볼 때마다 지금 시각을 기준으로 상태를 계산한다. 그래서 새로 고침해도 같은 결과가 나온다.
//
//   접수(PENDING) ─ 아웃박스가 집어 감 ─▶ 반영 중(SENT) ─ 청크가 하나씩 처리됨 ─▶ 완료(SUCCESS) / 실패(FAILED)
//                                                                              실패 청크 재전송 ─▶ 다시 반영 중

import type { Chunk, ChunkStatus, CurriculumTree, Progress, DeploymentDetail, DeploymentStatus } from '../types'
import { planChunks } from './content'

/** 아웃박스 스케줄러가 집어 가기까지 걸리는 시간 */
export const ACCEPT_DELAY = 1200
/** 청크 하나가 처리되는 데 걸리는 시간 */
export const CHUNK_TICK = 220

export interface Attempt {
  /** 처리가 끝나는 시각(ms) */
  at: number
  ok: boolean
  error?: string
}

export interface MockChunk {
  label: string
  subjectId: string | null
  attempts: Attempt[]
}

export interface MockDeployment {
  deploymentId: string
  sourceConfigId: number
  sourceId: string
  targetId: number
  createdBy: string
  createdAt: number
  /** 요청 시점의 원본. 대상에 무엇이 들어갔는지 계산할 때 쓴다. */
  tree: CurriculumTree
  chunks: MockChunk[]
}

/** 실패 청크 번호와 오류 메시지 */
export type FailurePlan = Record<number, string>

export function createMockDeployment(
  base: Omit<MockDeployment, 'chunks'>,
  failures: FailurePlan = {},
): MockDeployment {
  const start = base.createdAt + ACCEPT_DELAY
  const chunks = planChunks(base.tree).map((plan, offset) => {
    const error = failures[offset]
    return {
      ...plan,
      attempts: [{ at: start + (offset + 1) * CHUNK_TICK, ok: error === undefined, ...(error ? { error } : {}) }],
    }
  })
  return { ...base, chunks }
}

interface ChunkState {
  status: ChunkStatus
  processedAt: number | null
  error: string | null
}

function chunkState(chunk: MockChunk, now: number): ChunkState {
  const done = chunk.attempts.filter((a) => a.at <= now)
  const last = done.at(-1)
  const waiting = chunk.attempts.some((a) => a.at > now)
  if (!last || waiting) return { status: 'PENDING', processedAt: last?.at ?? null, error: null }
  return { status: last.ok ? 'SUCCESS' : 'FAILED', processedAt: last.at, error: last.ok ? null : (last.error ?? null) }
}

/** 이 청크가 now 시점까지 한 번이라도 성공했는지. 대상 데이터를 계산할 때 쓴다. */
export function succeededBy(chunk: MockChunk, now: number): number | null {
  const ok = chunk.attempts.find((a) => a.ok && a.at <= now)
  return ok ? ok.at : null
}

const iso = (ms: number) => new Date(ms).toISOString()

export function statusOf(t: MockDeployment, now: number): DeploymentStatus {
  if (now < t.createdAt + ACCEPT_DELAY) return 'PENDING'
  const states = t.chunks.map((c) => chunkState(c, now).status)
  if (states.includes('PENDING')) return 'SENT'
  return states.includes('FAILED') ? 'FAILED' : 'SUCCESS'
}

export function simulate(t: MockDeployment, now: number): DeploymentDetail {
  const chunks: Chunk[] = t.chunks.map((c, offset) => {
    const state = chunkState(c, now)
    return {
      chunkOffset: offset,
      label: c.label,
      status: state.status,
      processedAt: state.processedAt === null ? null : iso(state.processedAt),
      errorMessage: state.error,
      retryCount: c.attempts.length - 1,
    }
  })

  const progress: Progress = {
    total: chunks.length,
    succeeded: chunks.filter((c) => c.status === 'SUCCESS').length,
    failed: chunks.filter((c) => c.status === 'FAILED').length,
  }

  const status = statusOf(t, now)
  const retried = chunks.some((c) => c.retryCount > 0)
  let resultMessage: string | null = null
  if (status === 'SUCCESS') {
    resultMessage = `과목 ${chunks.length - 1}개를 포함해 청크 ${chunks.length}개를 모두 반영했습니다.${retried ? ' (재전송 포함)' : ''}`
  } else if (status === 'FAILED') {
    resultMessage = `청크 ${progress.failed}개가 반영되지 않았습니다. 실패한 청크만 다시 보낼 수 있습니다.`
  }

  const events = t.chunks.flatMap((c) => c.attempts.map((a) => a.at)).filter((at) => at <= now)
  const updatedAt = Math.max(t.createdAt, ...events)

  return {
    deploymentId: t.deploymentId,
    sourceConfigId: t.sourceConfigId,
    sourceId: t.sourceId,
    sourceTitle: t.tree.title,
    targetId: t.targetId,
    status,
    progress,
    createdBy: t.createdBy,
    createdAt: iso(t.createdAt),
    updatedAt: iso(updatedAt),
    resultMessage,
    chunks,
  }
}

/** 실패한 청크마다 새 시도를 붙인다. 붙인 개수를 돌려준다. 끝나지 않은 배포에는 붙이지 않는다. */
export function retryFailed(t: MockDeployment, now: number): number {
  if (statusOf(t, now) !== 'FAILED') return 0
  const failed = t.chunks.filter((c) => chunkState(c, now).status === 'FAILED')
  failed.forEach((chunk, i) => chunk.attempts.push({ at: now + ACCEPT_DELAY + (i + 1) * CHUNK_TICK * 2, ok: true }))
  return failed.length
}

/** 모든 시도가 끝나는 시각. 이 뒤로는 결과가 바뀌지 않는다. */
export function finishesAt(t: MockDeployment): number {
  return Math.max(t.createdAt + ACCEPT_DELAY, ...t.chunks.flatMap((c) => c.attempts.map((a) => a.at)))
}
