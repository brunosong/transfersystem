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

/** 과목 초안. 뼈대와 다른 애그리거트라 revision 을 따로 갖는다 */
export interface SubjectDraft {
  id: string
  semesterId: string
  title: string
  description: string | null
  revision: number
}

/** 목록의 한 줄. 목록 API 는 아직 없다. 생기면 이 모양으로 받는다 */
export interface CurriculumListItem {
  curriculumId: string
  title: string
  gradeCount: number
  revision: number
}

export interface CurriculumListPage {
  items: CurriculumListItem[]
  total: number
}

export function isConflict(error: unknown): boolean {
  return error instanceof ApiError && error.status === 409
}

/** 아직 서버에 없는 기능이다(목록처럼 화면만 먼저 만든 것). GET 이 없으면 405 가 온다 */
export function isNotBuiltYet(error: unknown): boolean {
  return error instanceof ApiError && (error.status === 404 || error.status === 405)
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
  list: (q: string, page: number, size: number) =>
    request<CurriculumListPage>(`${BASE}?${new URLSearchParams({ q, page: String(page), size: String(size) })}`),

  create: (title: string, description: string) =>
    request<{ curriculumId: string; revision: number }>(BASE, 'POST', { title, description }),

  get: (curriculumId: string) => request<SkeletonDraft>(`${BASE}/${id(curriculumId)}`),

  edit: (curriculumId: string, title: string, description: string, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}`, 'PUT', { title, description, revision }),

  addGrade: (curriculumId: string, title: string, revision: number) =>
    request<{ gradeId: string; revision: number }>(`${BASE}/${id(curriculumId)}/grades`, 'POST', { title, revision }),

  editGrade: (curriculumId: string, gradeId: string, title: string, description: string, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}/grades/${id(gradeId)}`, 'PUT', { title, description, revision }),

  listSubjects: (curriculumId: string) => request<SubjectDraft[]>(`${BASE}/${id(curriculumId)}/subjects`),

  createSubject: (curriculumId: string, semesterId: string, title: string) =>
    request<SubjectDraft>(`${BASE}/${id(curriculumId)}/subjects`, 'POST', { semesterId, title }),

  removeGrade: (curriculumId: string, gradeId: string, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}/grades/${id(gradeId)}?revision=${revision}`, 'DELETE'),

  editSemester: (curriculumId: string, gradeId: string, semesterId: string, title: string, description: string, revision: number) =>
    request<SkeletonDraft>(
      `${BASE}/${id(curriculumId)}/grades/${id(gradeId)}/semesters/${id(semesterId)}`,
      'PUT',
      { title, description, revision },
    ),

  addSemester: (curriculumId: string, gradeId: string, title: string, revision: number) =>
    request<{ semesterId: string; revision: number }>(
      `${BASE}/${id(curriculumId)}/grades/${id(gradeId)}/semesters`,
      'POST',
      { title, revision },
    ),
}
