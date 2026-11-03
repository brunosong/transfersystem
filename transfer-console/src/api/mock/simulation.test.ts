import { describe, expect, it } from 'vitest'
import { buildCurriculum, planChunks } from './content'
import { ACCEPT_DELAY, CHUNK_TICK, createMockTransfer, finishesAt, retryFailed, simulate } from './simulation'

const tree = buildCurriculum('CUR003')
const base = {
  transferId: 't-1',
  sourceConfigId: 1,
  sourceId: 'CUR003',
  targetId: 3,
  createdBy: '김운영',
  createdAt: 1_000_000,
  tree,
}

describe('청크 나누기', () => {
  it('뼈대 하나와 과목마다 하나씩 나눈다', () => {
    const chunks = planChunks(tree)
    expect(tree.counts.subjects).toBe(40)
    expect(chunks).toHaveLength(41)
    expect(chunks[0].subjectId).toBeNull()
    expect(chunks[1].label).toBe('성인 1학년 · 1학기 · 국어')
    expect(tree.chunkCount).toBe(chunks.length)
  })
})

describe('simulate', () => {
  it('아웃박스가 집어 가기 전에는 대기다', () => {
    const t = createMockTransfer(base)
    const d = simulate(t, t.createdAt + 100)
    expect(d.status).toBe('PENDING')
    expect(d.progress).toEqual({ total: 41, succeeded: 0, failed: 0 })
  })

  it('청크가 순서대로 처리되는 동안 진행 중이다', () => {
    const t = createMockTransfer(base)
    const d = simulate(t, t.createdAt + ACCEPT_DELAY + CHUNK_TICK * 3)
    expect(d.status).toBe('SENT')
    expect(d.progress.succeeded).toBe(3)
    expect(d.chunks[3].status).toBe('PENDING')
  })

  it('모두 성공하면 완료다', () => {
    const t = createMockTransfer(base)
    const d = simulate(t, finishesAt(t))
    expect(d.status).toBe('SUCCESS')
    expect(d.progress).toEqual({ total: 41, succeeded: 41, failed: 0 })
    expect(d.resultMessage).toContain('모두 반영')
  })

  it('실패한 청크가 있으면 끝난 뒤 실패이고 오류 메시지를 담는다', () => {
    const t = createMockTransfer(base, { 3: 'Duplicate entry' })
    const d = simulate(t, finishesAt(t))
    expect(d.status).toBe('FAILED')
    expect(d.progress).toEqual({ total: 41, succeeded: 40, failed: 1 })
    expect(d.chunks[3]).toMatchObject({ status: 'FAILED', errorMessage: 'Duplicate entry', retryCount: 0 })
  })

  it('실패 청크만 다시 보내면 진행 중으로 돌아갔다가 완료된다', () => {
    const t = createMockTransfer(base, { 3: 'Duplicate entry', 7: 'Lock wait timeout' })
    const done = finishesAt(t)
    expect(retryFailed(t, done)).toBe(2)

    const during = simulate(t, done + 1)
    expect(during.status).toBe('SENT')
    expect(during.chunks[3]).toMatchObject({ status: 'PENDING', retryCount: 1 })
    expect(during.chunks[4].status).toBe('SUCCESS')

    const after = simulate(t, finishesAt(t))
    expect(after.status).toBe('SUCCESS')
    expect(after.chunks[7]).toMatchObject({ status: 'SUCCESS', retryCount: 1, errorMessage: null })
    expect(after.resultMessage).toContain('재전송')
  })

  it('끝나지 않았거나 실패가 없으면 다시 보내지 않는다', () => {
    const t = createMockTransfer(base, { 3: 'x' })
    expect(retryFailed(t, t.createdAt + ACCEPT_DELAY + CHUNK_TICK)).toBe(0)
    const ok = createMockTransfer(base)
    expect(retryFailed(ok, finishesAt(ok))).toBe(0)
  })
})
