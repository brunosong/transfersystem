import { httpApi } from './http'
import { mockApi } from './mock'
import type { ConsoleApi } from './types'

// VITE_API_MODE=mock 이면 브라우저 안의 모의 API, 아니면 실제 서비스를 호출한다.
export const api: ConsoleApi = import.meta.env.VITE_API_MODE === 'mock' ? mockApi : httpApi

export { ApiError, isNotFound } from './http'
export type * from './types'
