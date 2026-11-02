import { httpApi } from './http'
import { mockApi } from './mock'
import type { TransferApi } from './types'

// VITE_API_MODE=mock 이면 브라우저 안의 모의 API, 아니면 transfer-service 를 호출한다.
export const api: TransferApi = import.meta.env.VITE_API_MODE === 'mock' ? mockApi : httpApi

export { ApiError } from './http'
export type * from './types'
