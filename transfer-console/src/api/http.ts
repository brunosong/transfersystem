import type {
  CreateTransferRequest,
  CreateTransferResponse,
  Page,
  SourceConfig,
  StatusCounts,
  TransferApi,
  TransferDetail,
  TransferQuery,
  TransferSummary,
} from './types'

/** 백엔드가 돌려준 오류. ErrorDTO(code, message) 를 그대로 담는다. */
export class ApiError extends Error {
  constructor(
    readonly status: number,
    message: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response
  try {
    response = await fetch(path, {
      ...init,
      headers: { Accept: 'application/json', ...(init?.body ? { 'Content-Type': 'application/json' } : {}) },
    })
  } catch {
    throw new ApiError(0, 'transfer-service 에 연결할 수 없습니다.')
  }
  if (!response.ok) {
    const body = await response.json().catch(() => null)
    throw new ApiError(response.status, body?.message ?? `요청이 실패했습니다 (${response.status})`)
  }
  return response.json() as Promise<T>
}

export const httpApi: TransferApi = {
  mode: 'live',

  async health() {
    try {
      const response = await fetch('/healthcheck/ready')
      return response.ok
    } catch {
      return false
    }
  },

  listTransfers({ status, page = 0, size = 20 }: TransferQuery) {
    const params = new URLSearchParams({ page: String(page), size: String(size) })
    if (status) params.set('status', status)
    return request<Page<TransferSummary>>(`/api/v1/transfers?${params}`)
  },

  getTransfer(transferId) {
    return request<TransferDetail>(`/api/v1/transfers/${encodeURIComponent(transferId)}`)
  },

  countByStatus() {
    return request<StatusCounts>('/api/v1/transfers/stats')
  },

  listSourceConfigs() {
    return request<SourceConfig[]>('/api/v1/source-configs')
  },

  createTransfer(body: CreateTransferRequest) {
    return request<CreateTransferResponse>('/api/v1/transfers', { method: 'POST', body: JSON.stringify(body) })
  },
}
