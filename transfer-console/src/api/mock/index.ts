// 브라우저 안에서만 도는 모의 API. 백엔드 없이 화면 흐름을 확인할 때 쓴다. npm run dev:mock 으로 켠다.
//
// 모의 모드에서 일부러 실패를 일으키는 방법
//   - 커리큘럼 CUR-FAIL-01 을 배포하면 청크 두 개가 실패한다. 재전송하면 성공한다.
//   - 원본 저장소의 호스트에 "down" 이 들어가면 연결 테스트가 실패한다.
//   - 부트캠프·PROD 는 연결이 끊긴 상태로 시작한다.

import { ApiError } from '../http'
import type {
  ConnectionCheck,
  ConsoleApi,
  CurriculumDeployment,
  CurriculumSummary,
  SourceConfig,
  SourceConfigInput,
  StatusCounts,
  TargetCurriculum,
  TransferSummary,
} from '../types'
import { TRANSFER_STATUSES } from '../types'
import { curriculumIds, FAILING_CURRICULUM_ID, hasCurriculum } from './content'
import { createMockTransfer, type MockTransfer, retryFailed, simulate, statusOf } from './simulation'
import { type MockSource, save, sourceTree, state, targetTree } from './store'

function delay<T>(value: T): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(structuredClone(value)), 120 + Math.random() * 180))
}

function fail(status: number, message: string): Promise<never> {
  return new Promise((_, reject) => setTimeout(() => reject(new ApiError(status, message)), 150))
}

const iso = (ms: number) => new Date(ms).toISOString()

function publicSource({ catalog: _catalog, deleted: _deleted, ...rest }: MockSource): SourceConfig {
  return rest
}

function liveSources(): MockSource[] {
  return state.sources.filter((s) => !s.deleted)
}

function findSource(id: number): MockSource | undefined {
  return liveSources().find((s) => s.sourceConfigId === id)
}

function isActive(t: MockTransfer, now: number): boolean {
  const status = statusOf(t, now)
  return status === 'PENDING' || status === 'SENT' || status === 'PROCESSED'
}

function summaryOf(t: MockTransfer, now: number): TransferSummary {
  const { resultMessage: _r, chunks: _c, ...summary } = simulate(t, now)
  return summary
}

function byNewest(a: MockTransfer, b: MockTransfer): number {
  return b.createdAt - a.createdAt
}

function deploymentsOf(curriculumId: string, now: number): CurriculumDeployment[] {
  return state.targets.map((target) => {
    const history = state.transfers.filter((t) => t.sourceId === curriculumId && t.targetId === target.targetId).sort(byNewest)
    const last = history[0]
    const lastSuccess = history.map((t) => simulate(t, now)).find((d) => d.status === 'SUCCESS')
    return {
      targetId: target.targetId,
      last: last ? { transferId: last.transferId, status: statusOf(last, now), at: iso(last.createdAt) } : null,
      lastSucceededAt: lastSuccess?.updatedAt ?? null,
    }
  })
}

/** 모의 원본은 호스트에 down 이 들어가면 연결이 안 된다 */
function testSource(source: MockSource, now: number): ConnectionCheck {
  if (source.host.toLowerCase().includes('down') || source.port <= 0) {
    return { ok: false, message: `${source.host}:${source.port} 에 연결할 수 없습니다 (connection refused)`, documentCount: null, latencyMs: null, checkedAt: iso(now) }
  }
  const documentCount = source.sourceType === 'CURRICULUM' ? curriculumIds(source.catalog).length : 0
  return { ok: true, message: '연결됨', documentCount, latencyMs: 8 + Math.round(Math.random() * 30), checkedAt: iso(now) }
}

function validateSource(input: SourceConfigInput, creating: boolean): string | null {
  if (!input.name.trim()) return '이름을 입력하세요.'
  if (!input.host.trim()) return '호스트를 입력하세요.'
  if (!Number.isInteger(input.port) || input.port < 0 || input.port > 65535) return '포트가 올바르지 않습니다.'
  if (!input.database.trim()) return 'DB 이름을 입력하세요.'
  if (!input.tableOrCollection.trim()) return '컬렉션(테이블)을 입력하세요.'
  if (creating && !input.password) return '비밀번호를 입력하세요.'
  if (input.sourceType !== 'CURRICULUM') return '아직 커리큘럼만 배포할 수 있습니다.'
  return null
}

export const mockApi: ConsoleApi = {
  mode: 'mock',

  health: () => delay(true),

  // ─── 원본 저장소 ──────────────────────────────────────────────
  listSourceConfigs: () => delay(liveSources().map(publicSource)),

  createSourceConfig(input) {
    const error = validateSource(input, true)
    if (error) return fail(400, error)
    const { password: _password, ...fields } = input
    const source: MockSource = {
      ...fields,
      sourceConfigId: Math.max(0, ...state.sources.map((s) => s.sourceConfigId)) + 1,
      lastCheck: null,
      // 새로 등록한 원본에는 커리큘럼 10개가 들어 있다고 본다
      catalog: { curriculumCount: 10, includeFailing: true, revised: [] },
      deleted: false,
    }
    state.sources.push(source)
    save()
    return delay(publicSource(source))
  },

  updateSourceConfig(id, input) {
    const source = findSource(id)
    if (!source) return fail(404, `원본 저장소를 찾을 수 없습니다: ${id}`)
    const error = validateSource(input, false)
    if (error) return fail(400, error)
    const { password: _password, ...fields } = input
    const changedConnection = fields.host !== source.host || fields.port !== source.port || fields.database !== source.database
    Object.assign(source, fields)
    if (changedConnection) source.lastCheck = null
    save()
    return delay(publicSource(source))
  },

  deleteSourceConfig(id) {
    const source = findSource(id)
    if (!source) return fail(404, `원본 저장소를 찾을 수 없습니다: ${id}`)
    const now = Date.now()
    const related = state.transfers.filter((t) => t.sourceConfigId === id)
    if (related.some((t) => isActive(t, now))) return fail(409, '이 원본으로 진행 중인 배포가 있어 삭제할 수 없습니다.')
    if (related.length > 0) {
      source.deleted = true
      source.enabled = false
    } else {
      state.sources = state.sources.filter((s) => s.sourceConfigId !== id)
    }
    save()
    return delay({ softDeleted: related.length > 0 })
  },

  testSourceConfig(id) {
    const source = findSource(id)
    if (!source) return fail(404, `원본 저장소를 찾을 수 없습니다: ${id}`)
    source.lastCheck = testSource(source, Date.now())
    save()
    return delay(source.lastCheck)
  },

  // ─── 콘텐츠 ──────────────────────────────────────────────────
  listCurriculums(sourceId, { q, page = 0, size = 20 }) {
    const source = findSource(sourceId)
    if (!source) return fail(404, `원본 저장소를 찾을 수 없습니다: ${sourceId}`)
    if (testSource(source, Date.now()).ok === false) return fail(503, `원본 저장소(${source.name})에 연결할 수 없습니다.`)
    const now = Date.now()
    const keyword = q?.trim().toLowerCase() ?? ''
    const all: CurriculumSummary[] = curriculumIds(source.catalog)
      .map((curriculumId) => sourceTree(source, curriculumId))
      .filter((tree) => !keyword || tree.curriculumId.toLowerCase().includes(keyword) || tree.title.toLowerCase().includes(keyword))
      .map((tree) => {
        const deployments = deploymentsOf(tree.curriculumId, now)
        const latest = deployments
          .map((d) => d.last)
          .filter((d) => d !== null)
          .sort((a, b) => b.at.localeCompare(a.at))[0]
        return {
          curriculumId: tree.curriculumId,
          title: tree.title,
          subjectCount: tree.counts.subjects,
          deployedTargetCount: deployments.filter((d) => d.lastSucceededAt !== null).length,
          lastDeployment: latest ?? null,
        }
      })
    const items = all.slice(page * size, page * size + size)
    return delay({ items, page, size, totalItems: all.length, totalPages: Math.max(1, Math.ceil(all.length / size)) })
  },

  getCurriculum(sourceId, curriculumId) {
    const source = findSource(sourceId)
    if (!source) return fail(404, `원본 저장소를 찾을 수 없습니다: ${sourceId}`)
    if (!hasCurriculum(source.catalog, curriculumId)) return fail(404, `원본에 커리큘럼이 없습니다: ${curriculumId}`)
    return delay(sourceTree(source, curriculumId))
  },

  listCurriculumDeployments: (curriculumId) => delay(deploymentsOf(curriculumId, Date.now())),

  // ─── 대상 시스템 ─────────────────────────────────────────────
  listTargets: () => delay(state.targets),

  testTarget(targetId) {
    const target = state.targets.find((t) => t.targetId === targetId)
    if (!target) return fail(404, `대상 시스템을 찾을 수 없습니다: ${targetId}`)
    const now = Date.now()
    // 부트캠프·PROD 는 계속 끊겨 있다
    target.lastCheck =
      targetId === 2
        ? { ok: false, message: 'Communications link failure: Connection refused', documentCount: null, latencyMs: null, checkedAt: iso(now) }
        : { ok: true, message: '연결됨', documentCount: null, latencyMs: 5 + Math.round(Math.random() * 20), checkedAt: iso(now) }
    save()
    return delay(target.lastCheck)
  },

  setTargetEnabled(targetId, enabled) {
    const target = state.targets.find((t) => t.targetId === targetId)
    if (!target) return fail(404, `대상 시스템을 찾을 수 없습니다: ${targetId}`)
    target.enabled = enabled
    save()
    return delay(target)
  },

  listTargetCurriculums(targetId) {
    const now = Date.now()
    const ids = [...new Set(state.transfers.filter((t) => t.targetId === targetId).map((t) => t.sourceId))]
    const items: TargetCurriculum[] = ids
      .map((id) => targetTree(targetId, id, now))
      .filter((tree) => tree !== null)
      .map((tree) => ({ curriculumId: tree.curriculumId, title: tree.title, subjectCount: tree.counts.subjects, updatedAt: tree.updatedAt }))
      .sort((a, b) => b.updatedAt.localeCompare(a.updatedAt))
    return delay(items)
  },

  getTargetCurriculum(targetId, curriculumId) {
    const tree = targetTree(targetId, curriculumId, Date.now())
    if (!tree) return fail(404, `대상에 커리큘럼이 없습니다: ${curriculumId}`)
    return delay(tree)
  },

  // ─── 배포 ────────────────────────────────────────────────────
  listTransfers({ status, targetId, sourceId, createdBy, from, to, page = 0, size = 20 }) {
    const now = Date.now()
    const fromMs = from ? Date.parse(from) : -Infinity
    const toMs = to ? Date.parse(to) : Infinity
    const keyword = sourceId?.trim().toLowerCase() ?? ''
    const all = [...state.transfers]
      .sort(byNewest)
      .filter((t) => t.createdAt >= fromMs && t.createdAt < toMs)
      .filter((t) => targetId === undefined || t.targetId === targetId)
      .filter((t) => !keyword || t.sourceId.toLowerCase().includes(keyword))
      .filter((t) => !createdBy || t.createdBy === createdBy)
      .map((t) => summaryOf(t, now))
      .filter((t) => !status?.length || status.includes(t.status))
    const items = all.slice(page * size, page * size + size)
    return delay({ items, page, size, totalItems: all.length, totalPages: Math.max(1, Math.ceil(all.length / size)) })
  },

  getTransfer(transferId) {
    const found = state.transfers.find((t) => t.transferId === transferId)
    if (!found) return fail(404, `배포를 찾을 수 없습니다: ${transferId}`)
    return delay(simulate(found, Date.now()))
  },

  countByStatus({ from, to } = {}) {
    const now = Date.now()
    const fromMs = from ? Date.parse(from) : -Infinity
    const toMs = to ? Date.parse(to) : Infinity
    const counts = Object.fromEntries(TRANSFER_STATUSES.map((s) => [s, 0])) as StatusCounts
    for (const t of state.transfers) {
      if (t.createdAt >= fromMs && t.createdAt < toMs) counts[statusOf(t, now)]++
    }
    return delay(counts)
  },

  createTransfer(request, requestedBy) {
    const now = Date.now()
    const source = findSource(request.sourceConfigId)
    if (!source || !source.enabled) return fail(400, '사용할 수 있는 원본 저장소가 아닙니다.')
    if (source.sourceType !== 'CURRICULUM') return fail(422, `지원하지 않는 원본 유형입니다: ${source.sourceType}`)
    if (!hasCurriculum(source.catalog, request.sourceId)) return fail(404, `원본에 커리큘럼이 없습니다: ${request.sourceId}`)
    const target = state.targets.find((t) => t.targetId === request.dataMigrationInfoId)
    if (!target) return fail(400, '대상 시스템을 찾을 수 없습니다.')
    if (!target.enabled) return fail(422, '배포가 허용되지 않은 대상입니다.')
    const running = state.transfers.find((t) => t.sourceId === request.sourceId && t.targetId === target.targetId && isActive(t, now))
    if (running) return fail(409, '같은 커리큘럼을 같은 대상으로 보내는 배포가 진행 중입니다.')

    const failing = request.sourceId === FAILING_CURRICULUM_ID
    const transfer = createMockTransfer(
      {
        transferId: crypto.randomUUID(),
        sourceConfigId: source.sourceConfigId,
        sourceId: request.sourceId,
        targetId: target.targetId,
        createdBy: requestedBy,
        createdAt: now,
        tree: sourceTree(source, request.sourceId),
      },
      failing ? { 3: "Duplicate entry 'U9901031' for key 'unit.UK_unit_id'", 7: 'Lock wait timeout exceeded; try restarting transaction' } : {},
    )
    state.transfers.push(transfer)
    save()
    return delay({ transferId: transfer.transferId, chunkCount: transfer.chunks.length })
  },

  retryTransfer(transferId) {
    const found = state.transfers.find((t) => t.transferId === transferId)
    if (!found) return fail(404, `배포를 찾을 수 없습니다: ${transferId}`)
    const now = Date.now()
    if (isActive(found, now)) return fail(409, '아직 진행 중인 배포입니다. 끝난 뒤에 다시 보낼 수 있습니다.')
    const retriedChunks = retryFailed(found, now)
    if (retriedChunks === 0) return fail(422, '다시 보낼 실패 청크가 없습니다.')
    save()
    return delay({ retriedChunks })
  },
}
