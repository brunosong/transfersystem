import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { ApiError } from '../http'
import { FAILING_CURRICULUM_ID } from './content'
import { mockApi } from './index'
import { finishesAt } from './simulation'
import { reseed, state } from './store'

const NOW = new Date('2026-11-03T10:00:00').getTime()

beforeEach(() => {
  // 응답 지연(setTimeout)은 실제로 흐르게 두고 시각만 고정한다
  vi.useFakeTimers({ shouldAdvanceTime: true })
  vi.setSystemTime(NOW)
  reseed(NOW)
})

afterEach(() => vi.useRealTimers())

const request = (sourceId: string, targetId = 3) => ({ sourceConfigId: 1, sourceId, dataMigrationInfoId: targetId, transType: 'MESSAGING' as const })

async function rejects(promise: Promise<unknown>): Promise<ApiError> {
  try {
    await promise
  } catch (e) {
    return e as ApiError
  }
  throw new Error('거절되지 않았습니다')
}

describe('mockApi 배포', () => {
  it('같은 커리큘럼을 같은 대상으로 보내는 배포가 진행 중이면 409', async () => {
    // 시드의 CUR003 → 온라인캠퍼스·DEV 는 진행 중이다
    const error = await rejects(mockApi.createTransfer(request('CUR003'), '김운영'))
    expect(error.status).toBe(409)
    // 다른 대상은 된다
    await expect(mockApi.createTransfer(request('CUR003', 4), '김운영')).resolves.toMatchObject({ chunkCount: 41 })
  })

  it('배포 허용이 꺼진 대상이면 422', async () => {
    const error = await rejects(mockApi.createTransfer(request('CUR001', 2), '김운영'))
    expect(error.status).toBe(422)
  })

  it('원본에 없는 커리큘럼이면 404', async () => {
    const error = await rejects(mockApi.createTransfer(request('CUR999'), '김운영'))
    expect(error.status).toBe(404)
  })

  it('실패 재현용 커리큘럼은 실패하고, 재전송하면 대상 데이터가 원본과 같아진다', async () => {
    const { transferId } = await mockApi.createTransfer(request(FAILING_CURRICULUM_ID, 4), '이배포')
    const transfer = state.transfers.find((t) => t.transferId === transferId)!
    expect(transfer.createdBy).toBe('이배포')

    vi.setSystemTime(finishesAt(transfer))
    const failed = await mockApi.getTransfer(transferId)
    expect(failed.status).toBe('FAILED')
    expect(failed.progress.failed).toBe(2)

    const partial = await mockApi.getTargetCurriculum(4, FAILING_CURRICULUM_ID)
    expect(partial.counts.subjects).toBe(failed.chunks.length - 1 - 2)

    await expect(mockApi.retryTransfer(transferId, '이배포')).resolves.toEqual({ retriedChunks: 2 })
    vi.setSystemTime(finishesAt(transfer))
    expect((await mockApi.getTransfer(transferId)).status).toBe('SUCCESS')
    const full = await mockApi.getTargetCurriculum(4, FAILING_CURRICULUM_ID)
    expect(full.counts.subjects).toBe(failed.chunks.length - 1)
  })

  it('진행 중인 배포는 재전송할 수 없다', async () => {
    const running = state.transfers.find((t) => t.sourceId === 'CUR003')!
    const error = await rejects(mockApi.retryTransfer(running.transferId, '김운영'))
    expect(error.status).toBe(409)
  })
})

describe('mockApi 대상 데이터', () => {
  it('배포한 적 없는 커리큘럼은 404', async () => {
    const error = await rejects(mockApi.getTargetCurriculum(1, 'CUR020'))
    expect(error.status).toBe(404)
  })

  it('시드의 실패 배포는 실패한 과목이 대상에 없다', async () => {
    const tree = await mockApi.getTargetCurriculum(4, 'CUR012')
    expect(tree.counts.subjects).toBe(39)
  })

  it('대상별 배포 현황에 마지막 배포와 마지막 성공을 담는다', async () => {
    const deployments = await mockApi.listCurriculumDeployments('CUR001')
    const prod = deployments.find((d) => d.targetId === 1)!
    expect(prod.last?.status).toBe('SUCCESS')
    expect(prod.lastSucceededAt).not.toBeNull()
    expect(deployments.find((d) => d.targetId === 4)?.last).toBeNull()
  })
})

describe('mockApi 원본 저장소', () => {
  const input = {
    name: '새 원본',
    sourceType: 'CURRICULUM' as const,
    dbType: 'MONGO' as const,
    host: 'mongo-down.local',
    port: 27017,
    database: 'db',
    username: 'u',
    password: 'p',
    tableOrCollection: 'c',
    enabled: true,
  }

  it('응답에 비밀번호를 담지 않는다', async () => {
    const created = await mockApi.createSourceConfig(input)
    expect(created).not.toHaveProperty('password')
  })

  it('호스트에 down 이 들어가면 연결 테스트가 실패한다', async () => {
    const created = await mockApi.createSourceConfig(input)
    const check = await mockApi.testSourceConfig(created.sourceConfigId)
    expect(check.ok).toBe(false)
  })

  it('배포 이력이 없으면 지우고, 있으면 숨기기만 한다', async () => {
    const created = await mockApi.createSourceConfig(input)
    await expect(mockApi.deleteSourceConfig(created.sourceConfigId)).resolves.toEqual({ softDeleted: false })

    // 원본 1 에는 진행 중인 배포가 있다
    expect((await rejects(mockApi.deleteSourceConfig(1))).status).toBe(409)
    // 원본 1 의 진행 중 배포가 끝나면 숨기기만 한다
    vi.setSystemTime(NOW + 60_000)
    await expect(mockApi.deleteSourceConfig(1)).resolves.toEqual({ softDeleted: true })
    expect((await mockApi.listSourceConfigs()).map((s) => s.sourceConfigId)).not.toContain(1)
  })
})
