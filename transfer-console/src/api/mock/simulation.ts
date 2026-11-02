// 사가가 완성됐을 때의 흐름을 시간에 따라 흉내 낸다. 상태는 저장하지 않고
// "생성 시각으로부터 몇 ms 지났는가"만으로 계산하므로 같은 입력이면 항상 같은 결과가 나온다.
//
//   접수 → [온라인캠퍼스 APPLY] → [부트캠프 APPLY] → 성공
//                                     └ 실패 → [부트캠프 COMPENSATE] → [온라인캠퍼스 COMPENSATE] → 보상 완료

import type { Chunk, Progress, StepStatus, TransferDetail, TransferStatus, TransferStep } from '../types'

export type Scenario = 'success' | 'fail-bootcamp'

export interface MockTransfer {
  transferId: string
  sourceId: string
  sourceType: string
  sourceConfigId: number
  createdBy: string
  createdAt: number
  chunkCount: number
  scenario: Scenario
}

/** 아웃박스 스케줄러가 집어 가기까지 걸리는 시간 */
export const ACCEPT_DELAY = 1200
/** 청크 하나가 반영되는 데 걸리는 시간 */
export const APPLY_TICK = 650
/** 청크 하나를 되돌리는 데 걸리는 시간 */
export const COMPENSATE_TICK = 450

const ONLINE_CAMPUS = { id: 1, system: 'BRUNOSONG_ONLINE_CAMPUS' }
const BOOTCAMP = { id: 2, system: 'BRUNOSONG_BOOTCAMP' }

/** 부트캠프에서 실패하는 청크 순번. 앞의 청크들은 이미 반영돼 보상 대상이 된다. */
export function failingChunk(chunkCount: number): number {
  return Math.max(1, Math.ceil(chunkCount / 2))
}

interface Phase {
  action: 'APPLY' | 'COMPENSATE'
  target: { id: number; system: string }
  chunks: number
  tick: number
  /** 이 번호의 청크에서 실패한다 (1부터) */
  failAt?: number
}

function phasesOf(t: MockTransfer): Phase[] {
  const n = t.chunkCount
  if (t.scenario === 'success') {
    return [
      { action: 'APPLY', target: ONLINE_CAMPUS, chunks: n, tick: APPLY_TICK },
      { action: 'APPLY', target: BOOTCAMP, chunks: n, tick: APPLY_TICK },
    ]
  }
  const failAt = failingChunk(n)
  return [
    { action: 'APPLY', target: ONLINE_CAMPUS, chunks: n, tick: APPLY_TICK },
    { action: 'APPLY', target: BOOTCAMP, chunks: n, tick: APPLY_TICK, failAt },
    // 부트캠프는 실패 전까지 반영된 청크만, 온라인캠퍼스는 전부 되돌린다. 나중 단계부터 되돌린다.
    { action: 'COMPENSATE', target: BOOTCAMP, chunks: failAt - 1, tick: COMPENSATE_TICK },
    { action: 'COMPENSATE', target: ONLINE_CAMPUS, chunks: n, tick: COMPENSATE_TICK },
  ]
}

function iso(ms: number): string {
  return new Date(ms).toISOString()
}

export function simulate(t: MockTransfer, now: number): TransferDetail {
  const steps: TransferStep[] = []
  let cursor = t.createdAt + ACCEPT_DELAY
  let failedAt: number | null = null
  let lastEventAt = t.createdAt

  for (const [index, phase] of phasesOf(t).entries()) {
    const startedAt = cursor
    const lastChunk = phase.failAt ?? phase.chunks
    const chunks: Chunk[] = []

    for (let i = 1; i <= lastChunk; i++) {
      const at = startedAt + i * phase.tick
      if (at > now) break
      const failed = phase.failAt === i
      chunks.push({
        chunkOffset: i,
        status: phase.action === 'COMPENSATE' ? 'COMPENSATED' : failed ? 'FAILED' : 'SUCCESS',
        processedAt: iso(at),
      })
      lastEventAt = at
      if (failed) failedAt = at
    }

    const finishedAt = startedAt + lastChunk * phase.tick
    const started = now >= startedAt
    const finished = now >= finishedAt
    let status: StepStatus
    if (phase.action === 'COMPENSATE') {
      status = !started ? 'PENDING' : finished ? 'COMPENSATED' : 'COMPENSATING'
    } else if (phase.failAt && finished) {
      status = 'FAILED'
    } else {
      status = !started ? 'PENDING' : finished ? 'SUCCEEDED' : 'PROCESSING'
    }

    const succeeded = chunks.filter((c) => c.status === 'SUCCESS' || c.status === 'COMPENSATED').length
    steps.push({
      order: index + 1,
      action: phase.action,
      targetId: phase.target.id,
      targetSystem: phase.target.system,
      status,
      progress: { total: phase.chunks, succeeded, failed: chunks.filter((c) => c.status === 'FAILED').length },
      chunks,
    })
    cursor = finishedAt
  }

  // 아직 시작하지 않은 보상 단계는 실패가 나기 전까지 화면에 보이지 않아야 한다
  const visibleSteps = failedAt === null ? steps.filter((s) => s.action === 'APPLY') : steps

  const applySteps = steps.filter((s) => s.action === 'APPLY')
  const progress: Progress = {
    total: applySteps.reduce((sum, s) => sum + s.progress.total, 0),
    succeeded: applySteps.reduce((sum, s) => sum + s.progress.succeeded, 0),
    failed: applySteps.reduce((sum, s) => sum + s.progress.failed, 0),
  }

  const compensation = steps.filter((s) => s.action === 'COMPENSATE')
  const compensated = compensation.length > 0 && compensation.every((s) => s.status === 'COMPENSATED')
  let status: TransferStatus
  let resultMessage: string | null = null
  if (failedAt !== null) {
    status = 'FAILED'
    resultMessage = compensated
      ? `부트캠프 ${failingChunk(t.chunkCount)}번 청크 반영에 실패해 반영된 데이터를 모두 이전 상태로 되돌렸습니다.`
      : `부트캠프 ${failingChunk(t.chunkCount)}번 청크 반영에 실패했습니다. 반영된 데이터를 되돌리는 중입니다.`
  } else if (applySteps.every((s) => s.status === 'SUCCEEDED')) {
    status = 'SUCCESS'
  } else if (now < t.createdAt + ACCEPT_DELAY) {
    status = 'PENDING'
  } else {
    status = 'SENT'
  }

  return {
    transferId: t.transferId,
    sourceId: t.sourceId,
    sourceType: t.sourceType,
    sourceConfigId: t.sourceConfigId,
    status,
    resultMessage,
    progress,
    createdBy: t.createdBy,
    createdAt: iso(t.createdAt),
    updatedAt: iso(Math.min(now, Math.max(lastEventAt, t.createdAt))),
    steps: visibleSteps,
  }
}

/** 모든 단계가 끝나는 시각. 이 뒤로는 결과가 바뀌지 않는다. */
export function finishesAt(t: MockTransfer): number {
  return phasesOf(t).reduce(
    (at, phase) => at + (phase.failAt ?? phase.chunks) * phase.tick,
    t.createdAt + ACCEPT_DELAY,
  )
}
