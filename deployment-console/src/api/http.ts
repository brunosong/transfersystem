import type {
  ConnectionCheck,
  ConsoleApi,
  CreateDeploymentResponse,
  CurriculumDeployment,
  CurriculumSummary,
  CurriculumTree,
  DeleteSourceConfigResult,
  Page,
  RetryDeploymentResponse,
  SourceConfig,
  StatusCounts,
  Target,
  TargetCurriculum,
  TargetCurriculumTree,
  DeploymentDetail,
  DeploymentSummary,
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

export function isNotFound(error: unknown): boolean {
  return error instanceof ApiError && error.status === 404
}

interface RequestOptions {
  method?: string
  body?: unknown
  requestedBy?: string
}

async function request<T>(path: string, { method = 'GET', body, requestedBy }: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  // 한글 이름도 헤더에 실을 수 있게 인코딩한다
  if (requestedBy) headers['X-Requested-By'] = encodeURIComponent(requestedBy)

  let response: Response
  try {
    response = await fetch(path, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) })
  } catch {
    throw new ApiError(0, '서버에 연결할 수 없습니다.')
  }
  if (!response.ok) {
    const error = await response.json().catch(() => null)
    throw new ApiError(response.status, error?.message ?? `요청이 실패했습니다 (${response.status})`)
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

function query(params: Record<string, string | number | string[] | undefined>): string {
  const search = new URLSearchParams()
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === '') continue
    if (Array.isArray(value)) value.forEach((v) => search.append(key, v))
    else search.set(key, String(value))
  }
  const text = search.toString()
  return text ? `?${text}` : ''
}

const id = encodeURIComponent

export const httpApi: ConsoleApi = {
  mode: 'live',

  async health() {
    try {
      return (await fetch('/healthcheck/ready')).ok
    } catch {
      return false
    }
  },

  // 원본 저장소 ────────────────────────────────────────────
  listSourceConfigs: () => request<SourceConfig[]>('/api/v1/source-configs'),
  createSourceConfig: (input) => request<SourceConfig>('/api/v1/source-configs', { method: 'POST', body: input }),
  updateSourceConfig: (sourceId, input) =>
    request<SourceConfig>(`/api/v1/source-configs/${sourceId}`, { method: 'PUT', body: input }),
  deleteSourceConfig: (sourceId) =>
    request<DeleteSourceConfigResult>(`/api/v1/source-configs/${sourceId}`, { method: 'DELETE' }),
  testSourceConfig: (sourceId) =>
    request<ConnectionCheck>(`/api/v1/source-configs/${sourceId}/connection-test`, { method: 'POST' }),

  // 콘텐츠 ──────────────────────────────────────────────────
  listCurriculums: (sourceId, { q, page = 0, size = 20 }) =>
    request<Page<CurriculumSummary>>(`/api/v1/source-configs/${sourceId}/curriculums${query({ q, page, size })}`),
  getCurriculum: (sourceId, curriculumId) =>
    request<CurriculumTree>(`/api/v1/source-configs/${sourceId}/curriculums/${id(curriculumId)}`),
  listCurriculumDeployments: (curriculumId) =>
    request<CurriculumDeployment[]>(`/api/v1/curriculums/${id(curriculumId)}/deployments`),

  // 대상 시스템 (target-agent) ────────────────────
  listTargets: () => request<Target[]>('/api/v1/targets'),
  testTarget: (targetId) => request<ConnectionCheck>(`/api/v1/targets/${targetId}/connection-test`, { method: 'POST' }),
  setTargetEnabled: (targetId, enabled) =>
    request<Target>(`/api/v1/targets/${targetId}`, { method: 'PATCH', body: { enabled } }),
  listTargetCurriculums: (targetId) => request<TargetCurriculum[]>(`/api/v1/targets/${targetId}/curriculums`),
  getTargetCurriculum: (targetId, curriculumId) =>
    request<TargetCurriculumTree>(`/api/v1/targets/${targetId}/curriculums/${id(curriculumId)}`),

  // 배포 ────────────────────────────────────────────────────
  listDeployments: ({ status, targetId, sourceId, createdBy, from, to, page = 0, size = 20 }) =>
    request<Page<DeploymentSummary>>(
      `/api/v1/deployments${query({ status, targetId, sourceId, createdBy, from, to, page, size })}`,
    ),
  getDeployment: (deploymentId) => request<DeploymentDetail>(`/api/v1/deployments/${id(deploymentId)}`),
  countByStatus: ({ from, to } = {}) => request<StatusCounts>(`/api/v1/deployments/stats${query({ from, to })}`),
  createDeployment: (body, requestedBy) =>
    request<CreateDeploymentResponse>('/api/v1/deployments', { method: 'POST', body, requestedBy }),
  retryDeployment: (deploymentId, requestedBy) =>
    request<RetryDeploymentResponse>(`/api/v1/deployments/${id(deploymentId)}/retry`, { method: 'POST', requestedBy }),
}
