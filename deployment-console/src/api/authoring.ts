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
  /** 커리큘럼끼리의 정렬 순서. 작을수록 앞이다 */
  sortOrder: number
  grades: Grade[]
  revision: number
}

/** 과목 초안. 뼈대와 다른 애그리거트라 revision 을 따로 갖는다 */
export interface Lesson {
  id: string
  title: string
  description: string | null
  /** 학습 수준 1~5. 배포가 반드시 요구하는 칸이다 */
  learningLevel: number
}

export interface Unit {
  id: string
  title: string
  description: string | null
  lessons: Lesson[]
}

export interface SubjectDraft {
  id: string
  semesterId: string
  title: string
  description: string | null
  /** 한 학기 안의 과목끼리의 정렬 순서. 작을수록 앞이다 */
  sortOrder: number
  units: Unit[]
  revision: number
}

/** 목록의 한 줄. 목록 API 는 아직 없다. 생기면 이 모양으로 받는다 */
export interface CurriculumListItem {
  curriculumId: string
  title: string
  sortOrder: number
  gradeCount: number
  /** 가장 최근 판 번호. 공개한 적이 없으면 null */
  lastVersion: number | null
  revision: number
}

export interface CurriculumListPage {
  items: CurriculumListItem[]
  total: number
}

/** 판 기록 한 줄. 어떤 초안(뼈대 revision, 과목마다 revision)으로 만들었는지 담는다 */
export interface VersionSummary {
  version: number
  publishedAt: string
  skeletonRevision: number
  subjectRevisions: Record<string, number>
}

/** 한 칸 옮기는 방향. 학년, 학기, 단원, 차시는 목록 안의 자리가 순서다 */
export type MoveDirection = 'UP' | 'DOWN'

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
  // 과목 지우기는 돌려줄 것이 없어 204 다
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

const BASE = '/api/v1/curricula'
const id = encodeURIComponent

export const authoringApi = {
  list: (q: string, page: number, size: number) =>
    request<CurriculumListPage>(`${BASE}?${new URLSearchParams({ q, page: String(page), size: String(size) })}`),

  /**
   * 공개. 화면이 지금 보고 있는 뼈대 revision 과 과목마다의 revision 을 보낸다.
   * 그사이 누가 고쳤으면 409, 바뀐 것이 없거나 초안이 어긋나면 422
   */
  publish: (curriculumId: string, skeletonRevision: number, subjectRevisions: Record<string, number>) =>
    request<{ version: number; publishedAt: string }>(`${BASE}/${id(curriculumId)}/publish`, 'POST', {
      skeletonRevision,
      subjectRevisions,
    }),

  listVersions: (curriculumId: string) => request<VersionSummary[]>(`${BASE}/${id(curriculumId)}/versions`),

  create: (title: string, description: string) =>
    request<{ curriculumId: string; revision: number }>(BASE, 'POST', { title, description }),

  get: (curriculumId: string) => request<SkeletonDraft>(`${BASE}/${id(curriculumId)}`),

  edit: (curriculumId: string, title: string, description: string, sortOrder: number, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}`, 'PUT', { title, description, sortOrder, revision }),

  moveGrade: (curriculumId: string, gradeId: string, direction: MoveDirection, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}/grades/${id(gradeId)}/move`, 'POST', { direction, revision }),

  moveSemester: (curriculumId: string, gradeId: string, semesterId: string, direction: MoveDirection, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}/grades/${id(gradeId)}/semesters/${id(semesterId)}/move`, 'POST', {
      direction,
      revision,
    }),

  /** revision 은 그 과목의 revision 이다 */
  moveUnit: (curriculumId: string, subjectId: string, unitId: string, direction: MoveDirection, revision: number) =>
    request<SubjectDraft>(`${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}/move`, 'POST', {
      direction,
      revision,
    }),

  /** revision 은 그 과목의 revision 이다 */
  moveLesson: (
    curriculumId: string,
    subjectId: string,
    unitId: string,
    lessonId: string,
    direction: MoveDirection,
    revision: number,
  ) =>
    request<SubjectDraft>(
      `${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}/lessons/${id(lessonId)}/move`,
      'POST',
      { direction, revision },
    ),

  addGrade: (curriculumId: string, title: string, revision: number) =>
    request<{ gradeId: string; revision: number }>(`${BASE}/${id(curriculumId)}/grades`, 'POST', { title, revision }),

  editGrade: (curriculumId: string, gradeId: string, title: string, description: string, revision: number) =>
    request<SkeletonDraft>(`${BASE}/${id(curriculumId)}/grades/${id(gradeId)}`, 'PUT', { title, description, revision }),

  listSubjects: (curriculumId: string) => request<SubjectDraft[]>(`${BASE}/${id(curriculumId)}/subjects`),

  createSubject: (curriculumId: string, semesterId: string, title: string) =>
    request<SubjectDraft>(`${BASE}/${id(curriculumId)}/subjects`, 'POST', { semesterId, title }),

  /** revision 은 그 과목의 revision 이다 */
  editSubject: (curriculumId: string, subjectId: string, title: string, description: string, sortOrder: number, revision: number) =>
    request<SubjectDraft>(`${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}`, 'PUT', {
      title,
      description,
      sortOrder,
      revision,
    }),

  /** revision 은 그 과목의 revision 이다 */
  addUnit: (curriculumId: string, subjectId: string, title: string, revision: number) =>
    request<{ unitId: string; revision: number }>(
      `${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units`,
      'POST',
      { title, revision },
    ),

  /** revision 은 그 과목의 revision 이다 */
  editUnit: (curriculumId: string, subjectId: string, unitId: string, title: string, description: string, revision: number) =>
    request<SubjectDraft>(`${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}`, 'PUT', {
      title,
      description,
      revision,
    }),

  /** revision 은 그 과목의 revision 이다 */
  editLesson: (
    curriculumId: string,
    subjectId: string,
    unitId: string,
    lessonId: string,
    title: string,
    description: string,
    learningLevel: number,
    revision: number,
  ) =>
    request<SubjectDraft>(
      `${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}/lessons/${id(lessonId)}`,
      'PUT',
      { title, description, learningLevel, revision },
    ),

  /** revision 은 그 과목의 revision 이다 */
  addLesson: (curriculumId: string, subjectId: string, unitId: string, title: string, revision: number) =>
    request<{ lessonId: string; revision: number }>(
      `${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}/lessons`,
      'POST',
      { title, revision },
    ),

  removeSemester: (curriculumId: string, gradeId: string, semesterId: string, revision: number) =>
    request<SkeletonDraft>(
      `${BASE}/${id(curriculumId)}/grades/${id(gradeId)}/semesters/${id(semesterId)}?revision=${revision}`,
      'DELETE',
    ),

  /** revision 은 그 과목의 revision 이다 */
  removeSubject: (curriculumId: string, subjectId: string, revision: number) =>
    request<void>(`${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}?revision=${revision}`, 'DELETE'),

  removeUnit: (curriculumId: string, subjectId: string, unitId: string, revision: number) =>
    request<SubjectDraft>(
      `${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}?revision=${revision}`,
      'DELETE',
    ),

  removeLesson: (curriculumId: string, subjectId: string, unitId: string, lessonId: string, revision: number) =>
    request<SubjectDraft>(
      `${BASE}/${id(curriculumId)}/subjects/${id(subjectId)}/units/${id(unitId)}/lessons/${id(lessonId)}?revision=${revision}`,
      'DELETE',
    ),

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
