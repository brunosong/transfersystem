import { describe, expect, it } from 'vitest'
import { ACCEPT_DELAY, APPLY_TICK, failingChunk, finishesAt, type MockTransfer, simulate } from './simulation'

const base: MockTransfer = {
  transferId: 't-1',
  sourceId: 'curriculum-1',
  sourceType: 'CURRICULUM',
  sourceConfigId: 1,
  createdBy: 'tester',
  createdAt: 1_000_000,
  chunkCount: 4,
  scenario: 'success',
}

describe('simulate', () => {
  it('아웃박스가 집어 가기 전에는 접수 상태이고 단계는 대기다', () => {
    const d = simulate(base, base.createdAt + 100)
    expect(d.status).toBe('PENDING')
    expect(d.steps.map((s) => s.status)).toEqual(['PENDING', 'PENDING'])
    expect(d.progress).toEqual({ total: 8, succeeded: 0, failed: 0 })
  })

  it('첫 대상의 청크가 순서대로 반영되는 동안 반영 중이다', () => {
    const d = simulate(base, base.createdAt + ACCEPT_DELAY + APPLY_TICK * 2)
    expect(d.status).toBe('SENT')
    expect(d.steps[0].status).toBe('PROCESSING')
    expect(d.steps[0].chunks.map((c) => c.chunkOffset)).toEqual([1, 2])
    expect(d.steps[1].status).toBe('PENDING')
  })

  it('두 대상 모두 끝나면 완료이고 보상 단계는 보이지 않는다', () => {
    const d = simulate(base, finishesAt(base))
    expect(d.status).toBe('SUCCESS')
    expect(d.steps.map((s) => [s.action, s.status])).toEqual([
      ['APPLY', 'SUCCEEDED'],
      ['APPLY', 'SUCCEEDED'],
    ])
    expect(d.progress).toEqual({ total: 8, succeeded: 8, failed: 0 })
  })

  it('부트캠프에서 실패하면 나중 단계부터 거꾸로 되돌린다', () => {
    const failing: MockTransfer = { ...base, scenario: 'fail-bootcamp' }
    const d = simulate(failing, finishesAt(failing))

    expect(d.status).toBe('FAILED')
    expect(d.steps.map((s) => [s.action, s.targetSystem, s.status])).toEqual([
      ['APPLY', 'BRUNOSONG_ONLINE_CAMPUS', 'SUCCEEDED'],
      ['APPLY', 'BRUNOSONG_BOOTCAMP', 'FAILED'],
      ['COMPENSATE', 'BRUNOSONG_BOOTCAMP', 'COMPENSATED'],
      ['COMPENSATE', 'BRUNOSONG_ONLINE_CAMPUS', 'COMPENSATED'],
    ])
    // 부트캠프는 실패 전까지 반영된 청크만 되돌린다
    expect(d.steps[2].progress.total).toBe(failingChunk(4) - 1)
    expect(d.resultMessage).toContain('되돌렸습니다')
  })

  it('실패 직후에는 되돌리는 중이라는 메시지를 낸다', () => {
    const failing: MockTransfer = { ...base, scenario: 'fail-bootcamp' }
    const failAt = base.createdAt + ACCEPT_DELAY + 4 * APPLY_TICK + failingChunk(4) * APPLY_TICK
    const d = simulate(failing, failAt)
    expect(d.status).toBe('FAILED')
    expect(d.resultMessage).toContain('되돌리는 중')
  })
})
