// 브라우저 안에서만 도는 모의 API. 백엔드 없이 화면을 띄우고 사가 흐름을 시연할 때 쓴다.
// npm run dev:mock 으로 켠다.

import { ApiError } from '../http'
import type { SourceConfig, StatusCounts, TransferApi, TransferSummary } from '../types'
import { TRANSFER_STATUSES } from '../types'
import { type MockTransfer, type Scenario, simulate } from './simulation'

const SOURCE_CONFIGS: SourceConfig[] = [
  { sourceConfigId: 1, sourceType: 'CURRICULUM', dbType: 'MONGO', host: 'mongo.learning.svc', port: 27017, database: 'learningDB', tableOrCollection: 'CurriculumData' },
  { sourceConfigId: 2, sourceType: 'EXAM_RESULT_DATA', dbType: 'MYSQL', host: 'mysql.exam.svc', port: 3306, database: 'exam', tableOrCollection: 'exam_results' },
  { sourceConfigId: 3, sourceType: 'CURRICULUM', dbType: 'MONGO', host: 'mongo-dev.learning.svc', port: 27017, database: 'learningDB_dev', tableOrCollection: 'CurriculumData' },
  { sourceConfigId: 4, sourceType: 'LEARNING_MATERIAL', dbType: 'MONGO', host: 'mongo.learning.svc', port: 27017, database: 'exampledb', tableOrCollection: 'learning_materials' },
]

const MINUTE = 60_000
const HOUR = 60 * MINUTE

function hash(text: string): number {
  let h = 0
  for (const ch of text) h = (h * 31 + ch.charCodeAt(0)) >>> 0
  return h
}

function chunkCountOf(sourceId: string): number {
  return 4 + (hash(sourceId) % 7)
}

function scenarioOf(sourceId: string): Scenario {
  return sourceId.toLowerCase().includes('fail') ? 'fail-bootcamp' : 'success'
}

function seed(now: number): MockTransfer[] {
  const rows: Array<[string, number, string?]> = [
    ['curriculum-2026-math-g3', 4],
    ['curriculum-2026-sci-g5-fail', 9],
    ['curriculum-2026-eng-g1', 26 * MINUTE],
    ['curriculum-2026-kor-g2', 2 * HOUR],
    ['curriculum-2025-art-g4-fail', 5 * HOUR],
    ['curriculum-2025-music-g6', 23 * HOUR],
    ['curriculum-2025-pe-g2', 2 * 24 * HOUR],
    ['curriculum-2025-math-g1', 3 * 24 * HOUR],
    ['curriculum-2025-sci-g2', 6 * 24 * HOUR],
  ]
  return rows.map(([sourceId, ago], i) => ({
    transferId: `0b8f${(hash(sourceId) % 0xffff).toString(16).padStart(4, '0')}-6d2a-4c1e-9f3b-${String(i).padStart(12, '0')}`,
    sourceId,
    sourceType: 'CURRICULUM',
    sourceConfigId: i % 3 === 2 ? 3 : 1,
    createdBy: i % 2 === 0 ? 'brunosong' : 'ops-admin',
    // 숫자가 작으면 "초 전"으로 보고 지금 진행 중인 전송을 만든다
    createdAt: now - (ago < 60 ? ago * 1000 : ago),
    chunkCount: chunkCountOf(sourceId),
    scenario: scenarioOf(sourceId),
  }))
}

const store: MockTransfer[] = seed(Date.now())

function delay<T>(value: T): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(value), 120 + Math.random() * 180))
}

function summaries(now: number): TransferSummary[] {
  return [...store]
    .sort((a, b) => b.createdAt - a.createdAt)
    .map((t) => {
      const d = simulate(t, now)
      return {
        transferId: d.transferId,
        sourceId: d.sourceId,
        sourceType: d.sourceType,
        status: d.status,
        progress: d.progress,
        createdBy: d.createdBy,
        createdAt: d.createdAt,
        updatedAt: d.updatedAt,
      }
    })
}

export const mockApi: TransferApi = {
  mode: 'mock',

  health: () => delay(true),

  listTransfers({ status, page = 0, size = 20 }) {
    const all = summaries(Date.now()).filter((t) => !status || t.status === status)
    const items = all.slice(page * size, page * size + size)
    return delay({ items, page, size, totalItems: all.length, totalPages: Math.ceil(all.length / size) })
  },

  getTransfer(transferId) {
    const found = store.find((t) => t.transferId === transferId)
    if (!found) return Promise.reject(new ApiError(404, `Transfer not found: ${transferId}`))
    return delay(simulate(found, Date.now()))
  },

  countByStatus() {
    const counts = Object.fromEntries(TRANSFER_STATUSES.map((s) => [s, 0])) as StatusCounts
    for (const t of summaries(Date.now())) counts[t.status]++
    return delay(counts)
  },

  listSourceConfigs: () => delay(SOURCE_CONFIGS),

  createTransfer(request) {
    const config = SOURCE_CONFIGS.find((c) => c.sourceConfigId === request.sourceConfigId)
    if (!config) return Promise.reject(new ApiError(400, '원본 설정을 찾을 수 없습니다.'))
    if (config.sourceType !== 'CURRICULUM' && config.sourceType !== 'EXAM_RESULT_DATA') {
      return Promise.reject(new ApiError(422, `지원하지 않는 원본 유형입니다: ${config.sourceType}`))
    }
    const transfer: MockTransfer = {
      transferId: crypto.randomUUID(),
      sourceId: request.sourceId,
      sourceType: config.sourceType,
      sourceConfigId: config.sourceConfigId,
      createdBy: 'brunosong',
      createdAt: Date.now(),
      chunkCount: chunkCountOf(request.sourceId),
      scenario: scenarioOf(request.sourceId),
    }
    store.push(transfer)
    return delay({ transferId: transfer.transferId, chunkCount: transfer.chunkCount })
  },
}
