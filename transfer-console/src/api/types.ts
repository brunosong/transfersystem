// 화면이 쓰는 API 의 모양. docs/console-plan.md 의 "필요한 API" 표와 1:1 로 맞춘다.
// transfer-service(원본, 배포)와 data-migration-service(대상) 두 곳을 부른다.

// ─── 공통 ────────────────────────────────────────────────────────────────

export interface Page<T> {
  items: T[]
  page: number
  size: number
  totalItems: number
  totalPages: number
}

/** 연결 테스트 결과. 목록에는 마지막 결과가 캐시돼 따라온다. */
export interface ConnectionCheck {
  ok: boolean
  message: string
  /** 원본이면 컬렉션의 문서 수 */
  documentCount: number | null
  latencyMs: number | null
  checkedAt: string
}

// ─── 원본 저장소 (transfer-service) ───────────────────────────────────────

export type SourceType = 'CURRICULUM' | 'EXAM_RESULT_DATA'
export type DbType = 'MONGO' | 'MYSQL'

export interface SourceConfig {
  sourceConfigId: number
  name: string
  sourceType: SourceType
  dbType: DbType
  host: string
  port: number
  database: string
  username: string
  tableOrCollection: string
  enabled: boolean
  lastCheck: ConnectionCheck | null
}

/** 추가·수정 요청. 수정할 때 password 를 비우면 기존 값을 유지한다. */
export interface SourceConfigInput {
  name: string
  sourceType: SourceType
  dbType: DbType
  host: string
  port: number
  database: string
  username: string
  password: string
  tableOrCollection: string
  enabled: boolean
}

export interface DeleteSourceConfigResult {
  /** 배포 이력이 있어 지우지 않고 비활성으로만 돌렸으면 true */
  softDeleted: boolean
}

// ─── 콘텐츠 (transfer-service 가 원본을 읽어 준다) ───────────────────────

export interface LevelCounts {
  grades: number
  semesters: number
  subjects: number
  units: number
  lessons: number
}

export interface SubjectNode {
  id: string
  title: string
  unitCount: number
  lessonCount: number
}

export interface SemesterNode {
  id: string
  title: string
  subjects: SubjectNode[]
}

export interface GradeNode {
  id: string
  title: string
  semesters: SemesterNode[]
}

/** 커리큘럼 트리. 문서가 커서 과목까지만 내려 주고 단원·차시는 개수만 준다. 대상 쪽 조회도 같은 모양이다. */
export interface CurriculumTree {
  curriculumId: string
  title: string
  description: string | null
  counts: LevelCounts
  /** 배포하면 나뉘는 청크 수: 뼈대 1 + 과목 수 */
  chunkCount: number
  grades: GradeNode[]
}

export interface CurriculumSummary {
  curriculumId: string
  title: string
  subjectCount: number
  /** 한 번이라도 성공한 대상 수 */
  deployedTargetCount: number
  lastDeployment: { transferId: string; status: TransferStatus; at: string } | null
}

export interface CurriculumQuery {
  q?: string
  page?: number
  size?: number
}

/** 커리큘럼 하나가 대상마다 마지막으로 어떻게 배포됐는지 */
export interface CurriculumDeployment {
  targetId: number
  /** 마지막 배포(성공·실패·진행 중 무엇이든) */
  last: { transferId: string; status: TransferStatus; at: string } | null
  /** 마지막으로 성공한 시각. 대상에 데이터가 있다는 뜻이다. */
  lastSucceededAt: string | null
}

// ─── 대상 시스템 (data-migration-service) ─────────────────────────────────

export type TargetSystem = 'BRUNOSONG_ONLINE_CAMPUS' | 'BRUNOSONG_BOOTCAMP'
export type TargetEnvironment = 'PROD' | 'DEV'

export interface Target {
  targetId: number
  system: TargetSystem
  environment: TargetEnvironment
  /** 비밀번호를 뺀 접속 주소 */
  dbUrl: string
  /** 꺼져 있으면 배포 요청을 받지 않는다 (점검 등) */
  enabled: boolean
  lastCheck: ConnectionCheck | null
}

export interface TargetCurriculum {
  curriculumId: string
  title: string
  subjectCount: number
  updatedAt: string
}

/** 대상에 들어 있는 커리큘럼 트리. 마지막으로 어느 원본에서 왔는지도 알려 준다. */
export interface TargetCurriculumTree extends CurriculumTree {
  sourceConfigId: number | null
  updatedAt: string
}

// ─── 배포 (transfer-service) ─────────────────────────────────────────────

/** transfer_log 상태. 화면에서는 대기·진행 중·완료·실패 네 묶음으로 보여 준다. */
export type TransferStatus = 'PENDING' | 'SENT' | 'PROCESSED' | 'SUCCESS' | 'FAILED'
export const TRANSFER_STATUSES: TransferStatus[] = ['PENDING', 'SENT', 'PROCESSED', 'SUCCESS', 'FAILED']

export type ChunkStatus = 'PENDING' | 'SUCCESS' | 'FAILED'

export interface Progress {
  total: number
  succeeded: number
  failed: number
}

export interface Chunk {
  /** 0 은 뼈대(커리큘럼·학년·학기), 1 부터 과목 */
  chunkOffset: number
  /** 운영자가 알아볼 수 있는 설명. 예: "중등 1학년 · 1학기 · 국어" */
  label: string
  status: ChunkStatus
  processedAt: string | null
  errorMessage: string | null
  retryCount: number
}

export interface TransferSummary {
  transferId: string
  sourceConfigId: number
  sourceId: string
  /** 요청 시점의 커리큘럼 제목 */
  sourceTitle: string | null
  targetId: number
  status: TransferStatus
  progress: Progress
  createdBy: string
  createdAt: string
  updatedAt: string
}

export interface TransferDetail extends TransferSummary {
  resultMessage: string | null
  chunks: Chunk[]
}

export interface TransferQuery {
  status?: TransferStatus[]
  targetId?: number
  /** 커리큘럼 ID 일부 */
  sourceId?: string
  createdBy?: string
  /** 요청 시각 범위 [from, to) ISO */
  from?: string
  to?: string
  page?: number
  size?: number
}

export type StatusCounts = Record<TransferStatus, number>

export interface StatsQuery {
  from?: string
  to?: string
}

export interface CreateTransferRequest {
  sourceConfigId: number
  sourceId: string
  dataMigrationInfoId: number
  transType: 'MESSAGING'
}

export interface CreateTransferResponse {
  transferId: string
  chunkCount: number
}

export interface RetryTransferResponse {
  retriedChunks: number
}

// ─── API ─────────────────────────────────────────────────────────────────

/** 화면이 쓰는 API. 실제 HTTP 구현과 브라우저 안 모의 구현이 같은 모양을 따른다. */
export interface ConsoleApi {
  readonly mode: 'live' | 'mock'
  health(): Promise<boolean>

  // 원본 저장소
  listSourceConfigs(): Promise<SourceConfig[]>
  createSourceConfig(input: SourceConfigInput): Promise<SourceConfig>
  updateSourceConfig(id: number, input: SourceConfigInput): Promise<SourceConfig>
  deleteSourceConfig(id: number): Promise<DeleteSourceConfigResult>
  testSourceConfig(id: number): Promise<ConnectionCheck>

  // 콘텐츠
  listCurriculums(sourceConfigId: number, query: CurriculumQuery): Promise<Page<CurriculumSummary>>
  getCurriculum(sourceConfigId: number, curriculumId: string): Promise<CurriculumTree>
  listCurriculumDeployments(curriculumId: string): Promise<CurriculumDeployment[]>

  // 대상 시스템
  listTargets(): Promise<Target[]>
  testTarget(targetId: number): Promise<ConnectionCheck>
  setTargetEnabled(targetId: number, enabled: boolean): Promise<Target>
  listTargetCurriculums(targetId: number): Promise<TargetCurriculum[]>
  /** 대상에 없으면 404 */
  getTargetCurriculum(targetId: number, curriculumId: string): Promise<TargetCurriculumTree>

  // 배포
  listTransfers(query: TransferQuery): Promise<Page<TransferSummary>>
  getTransfer(transferId: string): Promise<TransferDetail>
  countByStatus(query?: StatsQuery): Promise<StatusCounts>
  /** requestedBy 는 X-Requested-By 헤더로 간다 */
  createTransfer(request: CreateTransferRequest, requestedBy: string): Promise<CreateTransferResponse>
  retryTransfer(transferId: string, requestedBy: string): Promise<RetryTransferResponse>
}
