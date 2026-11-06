// 모의 모드의 저장소. 원본 저장소, 대상 시스템, 배포 이력을 들고 있고 브라우저 localStorage 에 남긴다.
// 새로 고침해도 직접 만든 원본과 배포가 그대로 있다. 사이드바의 "모의 데이터 초기화"로 처음 상태로 돌린다.

import type { ConnectionCheck, CurriculumTree, SourceConfig, Target, TargetCurriculumTree } from '../types'
import { buildCurriculum, type Catalog, countLevels } from './content'
import { createMockDeployment, type FailurePlan, type MockDeployment, retryFailed, succeededBy } from './simulation'

export interface MockSource extends SourceConfig {
  catalog: Catalog
  deleted: boolean
}

export interface MockState {
  version: number
  sources: MockSource[]
  targets: Target[]
  deployments: MockDeployment[]
}

const STORAGE_KEY = 'transfer-console.mock'
const VERSION = 3

const MINUTE = 60_000
const HOUR = 60 * MINUTE
const DAY = 24 * HOUR

const iso = (ms: number) => new Date(ms).toISOString()

function check(ok: boolean, message: string, at: number, documentCount: number | null = null): ConnectionCheck {
  return { ok, message, documentCount, latencyMs: ok ? 12 : null, checkedAt: iso(at) }
}

/** 원본 저장소의 커리큘럼. 원본이 고쳐진 커리큘럼은 고친 뒤의 모습이 나온다. */
export function sourceTree(source: MockSource, curriculumId: string): CurriculumTree {
  return buildCurriculum(curriculumId, source.catalog.revised.includes(curriculumId))
}

function seed(now: number): MockState {
  const sources: MockSource[] = [
    {
      sourceConfigId: 1,
      name: '저작DB (운영)',
      sourceType: 'CURRICULUM',
      dbType: 'MONGO',
      host: 'mongo.authoring.svc',
      port: 27017,
      database: 'learningDB',
      username: 'learning_reader',
      tableOrCollection: 'CurriculumData',
      enabled: true,
      lastCheck: check(true, '연결됨', now - 20 * MINUTE, 31),
      catalog: { curriculumCount: 30, includeFailing: true, revised: ['CUR002'] },
      deleted: false,
    },
    {
      sourceConfigId: 2,
      name: '저작DB (개발)',
      sourceType: 'CURRICULUM',
      dbType: 'MONGO',
      host: 'mongo-dev.authoring.svc',
      port: 27017,
      database: 'learningDB_dev',
      username: 'learning_reader',
      tableOrCollection: 'CurriculumData',
      enabled: true,
      lastCheck: check(true, '연결됨', now - 2 * HOUR, 13),
      catalog: { curriculumCount: 12, includeFailing: true, revised: [] },
      deleted: false,
    },
  ]

  const targets: Target[] = [
    { targetId: 1, system: 'BRUNOSONG_ONLINE_CAMPUS', environment: 'PROD', dbUrl: 'mysql://onlinecampus-prod-db:3306/onlinecampus', enabled: true, lastCheck: check(true, '연결됨', now - 20 * MINUTE) },
    { targetId: 2, system: 'BRUNOSONG_BOOTCAMP', environment: 'PROD', dbUrl: 'mysql://bootcamp-prod-db:3306/bootcamp', enabled: false, lastCheck: check(false, 'Communications link failure: Connection refused', now - 20 * MINUTE) },
    { targetId: 3, system: 'BRUNOSONG_ONLINE_CAMPUS', environment: 'DEV', dbUrl: 'mysql://onlinecampus-dev-db:3306/onlinecampus', enabled: true, lastCheck: check(true, '연결됨', now - 20 * MINUTE) },
    { targetId: 4, system: 'BRUNOSONG_BOOTCAMP', environment: 'DEV', dbUrl: 'mysql://bootcamp-dev-db:3306/bootcamp', enabled: true, lastCheck: check(true, '연결됨', now - 20 * MINUTE) },
  ]

  const DUPLICATE = "Duplicate entry 'U0101031' for key 'unit.UK_unit_id'"
  const LOCK = 'Lock wait timeout exceeded; try restarting transaction'

  // [커리큘럼, 대상, 몇 ms 전, 요청자, 실패 청크, 재전송했는지]
  const rows: Array<[string, number, number, string, FailurePlan?, boolean?]> = [
    ['CUR003', 3, 4_000, '김운영'],
    ['CUR012', 4, 9 * MINUTE, '이배포', { 3: DUPLICATE }],
    ['CUR001', 1, 30 * MINUTE, '김운영'],
    ['CUR001', 3, 52 * MINUTE, '김운영'],
    ['CUR004', 3, 3 * HOUR, '박콘텐츠'],
    ['CUR005', 1, 4 * HOUR, '김운영'],
    ['CUR005', 3, 5 * HOUR, '김운영'],
    ['CUR006', 4, 1 * DAY, '이배포', { 10: LOCK, 22: LOCK }, true],
    ['CUR007', 3, 1 * DAY + 2 * HOUR, '박콘텐츠'],
    ['CUR008', 1, 1 * DAY + 3 * HOUR, '김운영'],
    ['CUR008', 3, 1 * DAY + 4 * HOUR, '김운영'],
    ['CUR002', 1, 2 * DAY, '이배포'],
    ['CUR002', 3, 2 * DAY + HOUR, '이배포'],
    ['CUR009', 2, 3 * DAY, '김운영'],
    ['CUR010', 4, 3 * DAY + 2 * HOUR, '박콘텐츠'],
    ['CUR011', 3, 4 * DAY, '이배포'],
    ['CUR011', 3, 4 * DAY + HOUR, '이배포', { 5: DUPLICATE }],
    ['CUR013', 3, 6 * DAY, '박콘텐츠'],
    ['CUR014', 1, 6 * DAY + HOUR, '김운영'],
    ['CUR015', 3, 6 * DAY + 2 * HOUR, '김운영'],
  ]

  const deployments = rows.map(([curriculumId, targetId, ago, user, failures, retried], i) => {
    const createdAt = now - ago
    // 원본이 고쳐지기 전 모습으로 배포됐다
    const deployment = createMockDeployment(
      {
        deploymentId: `7c1e${pad4(i)}-3b9a-4f2d-8e61-${String(1000 + i).padStart(12, '0')}`,
        sourceConfigId: 1,
        sourceId: curriculumId,
        targetId,
        createdBy: user,
        createdAt,
        tree: buildCurriculum(curriculumId, false),
      },
      failures,
    )
    if (retried) retryFailed(deployment, createdAt + 10 * MINUTE)
    return deployment
  })

  return { version: VERSION, sources, targets, deployments }
}

function pad4(n: number): string {
  return n.toString(16).padStart(4, '0')
}

function load(): MockState {
  if (typeof localStorage !== 'undefined') {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (raw) {
        const parsed = JSON.parse(raw) as MockState
        if (parsed.version === VERSION) return parsed
      }
    } catch {
      // 깨진 값이면 새로 만든다
    }
  }
  return seed(Date.now())
}

export const state: MockState = load()

export function save(): void {
  if (typeof localStorage === 'undefined') return
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
  } catch {
    // 저장 공간이 꽉 차도 화면은 계속 돈다
  }
}

export function resetMockData(): void {
  if (typeof localStorage !== 'undefined') localStorage.removeItem(STORAGE_KEY)
}

/** 테스트용: 정해진 시각 기준으로 처음 상태를 다시 만든다 */
export function reseed(now: number): void {
  Object.assign(state, seed(now))
}

// ─── 대상 시스템에 들어간 데이터 ─────────────────────────────────────────
// 대상 DB 를 따로 두지 않고 배포 이력에서 계산한다. 청크가 성공한 시점에 그 청크의 내용이 업서트된다고 본다.

export function targetTree(targetId: number, curriculumId: string, now: number): TargetCurriculumTree | null {
  const history = state.deployments
    .filter((t) => t.targetId === targetId && t.sourceId === curriculumId)
    .sort((a, b) => a.createdAt - b.createdAt)

  type Applied = { at: number; deployment: MockDeployment }
  let skeleton: Applied | null = null
  const subjects = new Map<string, Applied>()

  for (const deployment of history) {
    for (const chunk of deployment.chunks) {
      const at = succeededBy(chunk, now)
      if (at === null) continue
      if (chunk.subjectId === null) {
        if (!skeleton || at >= skeleton.at) skeleton = { at, deployment }
      } else {
        const prev = subjects.get(chunk.subjectId)
        if (!prev || at >= prev.at) subjects.set(chunk.subjectId, { at, deployment })
      }
    }
  }

  const base = skeleton as Applied | null
  if (!base && subjects.size === 0) return null

  // 뼈대는 마지막으로 성공한 뼈대 청크의 모습, 과목은 과목마다 마지막으로 성공한 모습
  const frame = (base ?? [...subjects.values()][0]).deployment.tree
  const grades = frame.grades.map((grade) => ({
    ...grade,
    semesters: grade.semesters.map((semester) => ({
      ...semester,
      subjects: semester.subjects.flatMap((subject) => {
        const applied = subjects.get(subject.id)
        if (!applied) return []
        const found = applied.deployment.tree.grades
          .flatMap((g) => g.semesters)
          .flatMap((s) => s.subjects)
          .find((s) => s.id === subject.id)
        return found ? [found] : []
      }),
    })),
  }))

  const counts = countLevels(grades)
  const lastAt = Math.max(base?.at ?? 0, ...[...subjects.values()].map((a) => a.at))
  const last = history.at(-1)
  return {
    curriculumId,
    title: frame.title,
    description: frame.description,
    counts,
    chunkCount: 1 + counts.subjects,
    grades,
    sourceConfigId: last?.sourceConfigId ?? null,
    updatedAt: iso(lastAt),
  }
}
