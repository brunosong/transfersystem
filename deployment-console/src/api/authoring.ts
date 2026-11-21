// 저작 서비스(authoring-service) API. 배포 서비스와 다른 서버라 ConsoleApi 와 따로 둔다.
// 개발 서버는 /api/v1/curricula 를 저작 서비스(8002)로 넘긴다(vite.config.ts).
// 모의 모드는 아직 없다. 모의 모드에서는 이 화면이 서버에 붙지 못한다.

import { ApiError } from './http'

export interface Semester {
  id: string
  title: string
  description: string | null
}

export interface Grade {
  id: string
  title: string
  description: string | null
  semesters: Semester[]
}

/** 뼈대 초안. revision 은 낙관적 락 번호다. 고칠 때 이 값을 함께 보낸다 */
export interface SkeletonDraft {
  curriculumId: string
  title: string
  description: string | null
  grades: Grade[]
  revision: number
}

export function isConflict(error: unknown): boolean {
  return error instanceof ApiError && error.status === 409
}

async function request<T>(path: string, method = 'GET', body?: unknown): Promise<T> {
  let response: Response
  try {
    response = await fetch(path, {
      method,
      headers: body === undefined ? { Accept: 'application/json' } : { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new ApiError(0, '저작 서비스에 연결할 수 없습니다.')
  }
  if (!response.ok) {
    // 저작 서비스는 Spring 표준 오류(ProblemDetail)를 돌려준다. 이유는 detail 에 있다
    const error = await response.json().catch(() => null)
    throw new ApiError(response.status, error?.detail ?? `요청이 실패했습니다 (${response.status})`)
  }
  return response.json() as Promise<T>
}

const BASE = '/api/v1/curricula'
const id = encodeURIComponent

export const authoringApi = {
  create: (title: string, description: string) =>
    request<{ curriculumId: string; revision: number }>(BASE, 'POST', { title, description }),

  get: (curriculumId: string) => request<SkeletonDraft>(`${BASE}/${id(curriculumId)}`),

  edit: (curriculumId: string, title: string, description: string, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}`, 'PUT', { title, description, revision }),

  addGrade: (curriculumId: string, title: string, revision: number) =>
    request<{ gradeId: string; revision: number }>(`${BASE}/${id(curriculumId)}/grades`, 'POST', { title, revision }),

  addSemester: (curriculumId: string, gradeId: string, title: string, revision: number) =>
    request<{ semesterId: string; revision: number }>(
      `${BASE}/${id(curriculumId)}/grades/${id(gradeId)}/semesters`,
      'POST',
      { title, revision },
    ),
}
