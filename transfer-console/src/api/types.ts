// transfer-service 의 조회 API 응답 모양. 백엔드 dto/query 패키지의 record 와 1:1 로 맞춘다.

/** 전송(transfer_log) 상태. 목록 필터에 쓰는 값이다. */
export type TransferStatus = 'PENDING' | 'SENT' | 'PROCESSED' | 'SUCCESS' | 'FAILED'

export const TRANSFER_STATUSES: TransferStatus[] = ['PENDING', 'SENT', 'PROCESSED', 'SUCCESS', 'FAILED']

/** 사가 단계 상태. 보상 상태는 사가가 들어오면 채워진다. */
export type StepStatus = 'PENDING' | 'PROCESSING' | 'SUCCEEDED' | 'FAILED' | 'COMPENSATING' | 'COMPENSATED'

export type StepAction = 'APPLY' | 'COMPENSATE'

export interface Progress {
  total: number
  succeeded: number
  failed: number
}

export interface Chunk {
  chunkOffset: number
  status: string
  processedAt: string | null
}

export interface TransferStep {
  order: number
  action: StepAction
  targetId: number | null
  targetSystem: string | null
  status: StepStatus
  progress: Progress
  chunks: Chunk[]
}

export interface TransferSummary {
  transferId: string
  sourceId: string
  sourceType: string | null
  status: TransferStatus
  progress: Progress
  createdBy: string
  createdAt: string
  updatedAt: string
}

export interface TransferDetail {
  transferId: string
  sourceId: string
  sourceType: string | null
  sourceConfigId: number
  status: TransferStatus
  resultMessage: string | null
  progress: Progress
  createdBy: string
  createdAt: string
  updatedAt: string
  steps: TransferStep[]
}

export interface Page<T> {
  items: T[]
  page: number
  size: number
  totalItems: number
  totalPages: number
}

export interface SourceConfig {
  sourceConfigId: number
  sourceType: string
  dbType: string
  host: string
  port: number
  database: string
  tableOrCollection: string
}

export type StatusCounts = Record<TransferStatus, number>

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

export interface TransferQuery {
  status?: TransferStatus
  page?: number
  size?: number
}

/** 화면이 쓰는 API. 실제 HTTP 구현과 브라우저 안 모의 구현이 같은 모양을 따른다. */
export interface TransferApi {
  readonly mode: 'live' | 'mock'
  health(): Promise<boolean>
  listTransfers(query: TransferQuery): Promise<Page<TransferSummary>>
  getTransfer(transferId: string): Promise<TransferDetail>
  countByStatus(): Promise<StatusCounts>
  listSourceConfigs(): Promise<SourceConfig[]>
  createTransfer(request: CreateTransferRequest): Promise<CreateTransferResponse>
}
