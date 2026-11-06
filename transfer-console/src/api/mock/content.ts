// 원본 저장소에 들어 있는 커리큘럼을 흉내 낸다.
// ID 규칙과 제목은 deployment-service 의 시드 생성기(LearningMaterialDataGenerator)를 따른다.
// 같은 입력이면 항상 같은 트리가 나오므로 저장하지 않고 그때그때 만든다.

import type { CurriculumTree, GradeNode, LevelCounts } from '../types'

const CURRICULUM_TITLES = ['초등 교육 과정', '중등 교육 과정', '고등 교육 과정', '성인 기초 과정', '특수 교육 과정']
const GRADE_PREFIXES = ['초등', '중등', '고등', '성인']
const SUBJECTS = ['국어', '수학', '영어', '과학', '사회']

/** 일부 청크가 실패하는 커리큘럼. 모의 모드에서 실패와 재전송을 보여 줄 때 쓴다. */
export const FAILING_CURRICULUM_ID = 'CUR-FAIL-01'

export interface Catalog {
  /** CUR001 부터 몇 개가 있는지 */
  curriculumCount: number
  /** 실패 재현용 커리큘럼을 넣을지 */
  includeFailing: boolean
  /** 배포 뒤에 원본이 고쳐진 커리큘럼. 대상 데이터 확인에서 "변경됨"으로 보인다. */
  revised: string[]
}

const pad = (n: number, width: number) => String(n).padStart(width, '0')

function hash(text: string): number {
  let h = 0
  for (const ch of text) h = (h * 31 + ch.charCodeAt(0)) >>> 0
  return h
}

export function curriculumIds(catalog: Catalog): string[] {
  const ids = Array.from({ length: catalog.curriculumCount }, (_, i) => `CUR${pad(i + 1, 3)}`)
  return catalog.includeFailing ? [...ids, FAILING_CURRICULUM_ID] : ids
}

export function hasCurriculum(catalog: Catalog, curriculumId: string): boolean {
  return curriculumIds(catalog).includes(curriculumId)
}

export function countLevels(grades: GradeNode[]): LevelCounts {
  const counts: LevelCounts = { grades: grades.length, semesters: 0, subjects: 0, units: 0, lessons: 0 }
  for (const grade of grades) {
    counts.semesters += grade.semesters.length
    for (const semester of grade.semesters) {
      counts.subjects += semester.subjects.length
      for (const subject of semester.subjects) {
        counts.units += subject.unitCount
        counts.lessons += subject.lessonCount
      }
    }
  }
  return counts
}

export function buildCurriculum(curriculumId: string, revised = false): CurriculumTree {
  const failing = curriculumId === FAILING_CURRICULUM_ID
  // CUR003 → 3. 실패 재현용은 번호가 없으니 99 로 둔다.
  const index = failing ? 99 : Number(curriculumId.replace(/\D/g, '')) || 1
  const gradeCount = failing ? 2 : 4
  const semesterCount = 2
  const prefix = GRADE_PREFIXES[index % GRADE_PREFIXES.length]

  const grades: GradeNode[] = []
  for (let g = 1; g <= gradeCount; g++) {
    const gradeTitle = `${prefix} ${g}학년`
    const semesters = []
    for (let s = 1; s <= semesterCount; s++) {
      const subjects = SUBJECTS.map((subjectTitle, k) => {
        // 단원 수는 과목마다 조금씩 다르게, 차시는 단원당 20~25개
        const seed = hash(`${curriculumId}-${g}-${s}-${k}`)
        const unitCount = 4 + (seed % 3)
        const lessonCount = unitCount * (20 + (seed % 6))
        const title = revised && g === 1 && s === 1 && subjectTitle === '수학' ? '수학 (개정)' : subjectTitle
        return {
          id: `SUB${pad(index * 10000 + g * 1000 + s * 100 + k, 6)}`,
          title,
          unitCount: revised && g === 1 && s === 1 && k === 1 ? unitCount + 1 : unitCount,
          lessonCount,
        }
      })
      semesters.push({ id: `S${pad(index * 1000 + g * 100 + s, 5)}`, title: `${s}학기`, subjects })
    }
    grades.push({ id: `G${pad(index * 100 + g, 4)}`, title: gradeTitle, semesters })
  }

  const counts = countLevels(grades)
  return {
    curriculumId,
    title: failing ? '실패 재현용 커리큘럼' : `${CURRICULUM_TITLES[index % CURRICULUM_TITLES.length]} ${index}`,
    description: failing
      ? '모의 모드 전용. 배포하면 청크 두 개가 실패합니다. 실패 청크를 다시 보내면 성공합니다.'
      : `${prefix} 과정 ${gradeCount}개 학년의 교과 콘텐츠`,
    counts,
    chunkCount: 1 + counts.subjects,
    grades,
  }
}

export interface ChunkPlan {
  label: string
  /** 과목 청크면 과목 ID, 뼈대 청크면 null */
  subjectId: string | null
}

/** deployment-service 의 CurriculumSourceConverter 와 같은 순서로 청크를 나눈다. 0 은 뼈대, 그 뒤로 과목마다 하나. */
export function planChunks(tree: CurriculumTree): ChunkPlan[] {
  const chunks: ChunkPlan[] = [{ label: '뼈대 · 커리큘럼·학년·학기', subjectId: null }]
  for (const grade of tree.grades) {
    for (const semester of grade.semesters) {
      for (const subject of semester.subjects) {
        chunks.push({ label: `${grade.title} · ${semester.title} · ${subject.title}`, subjectId: subject.id })
      }
    }
  }
  return chunks
}
