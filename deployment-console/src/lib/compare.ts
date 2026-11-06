// 원본과 대상의 커리큘럼 트리를 비교한다. 대상 데이터 확인 화면이 쓴다.
// 비교는 ID 기준이다. 같은 ID 인데 제목이나 단원·차시 수가 다르면 "변경됨"으로 본다(배포 뒤에 원본이 고쳐진 경우).

import type { CurriculumTree, LevelCounts, SubjectNode } from '../api/types'

export type DiffKind = 'same' | 'missing' | 'changed' | 'extra'

export interface SubjectDiff {
  id: string
  kind: DiffKind
  source: SubjectNode | null
  target: SubjectNode | null
}

export interface SemesterDiff {
  id: string
  title: string
  subjects: SubjectDiff[]
}

export interface GradeDiff {
  id: string
  title: string
  semesters: SemesterDiff[]
}

export interface TreeDiff {
  counts: { level: keyof LevelCounts; label: string; source: number; target: number }[]
  grades: GradeDiff[]
  summary: Record<DiffKind, number>
}

const LEVELS: { level: keyof LevelCounts; label: string }[] = [
  { level: 'grades', label: '학년' },
  { level: 'semesters', label: '학기' },
  { level: 'subjects', label: '과목' },
  { level: 'units', label: '단원' },
  { level: 'lessons', label: '차시' },
]

function subjectKind(source: SubjectNode | null, target: SubjectNode | null): DiffKind {
  if (source && !target) return 'missing'
  if (!source && target) return 'extra'
  if (source && target && (source.title !== target.title || source.unitCount !== target.unitCount || source.lessonCount !== target.lessonCount)) {
    return 'changed'
  }
  return 'same'
}

export function compareTrees(source: CurriculumTree, target: CurriculumTree | null): TreeDiff {
  const targetSubjects = new Map<string, SubjectNode>()
  const targetBySemester = new Map<string, SubjectNode[]>()
  for (const grade of target?.grades ?? []) {
    for (const semester of grade.semesters) {
      targetBySemester.set(semester.id, semester.subjects)
      for (const subject of semester.subjects) targetSubjects.set(subject.id, subject)
    }
  }

  const summary: Record<DiffKind, number> = { same: 0, missing: 0, changed: 0, extra: 0 }
  const grades: GradeDiff[] = source.grades.map((grade) => ({
    id: grade.id,
    title: grade.title,
    semesters: grade.semesters.map((semester) => {
      const sourceIds = new Set(semester.subjects.map((s) => s.id))
      const subjects: SubjectDiff[] = semester.subjects.map((s) => {
        const t = targetSubjects.get(s.id) ?? null
        return { id: s.id, kind: subjectKind(s, t), source: s, target: t }
      })
      // 원본에서는 지워졌는데 대상에 남아 있는 과목
      for (const t of targetBySemester.get(semester.id) ?? []) {
        if (!sourceIds.has(t.id)) subjects.push({ id: t.id, kind: 'extra', source: null, target: t })
      }
      subjects.forEach((s) => summary[s.kind]++)
      return { id: semester.id, title: semester.title, subjects }
    }),
  }))

  return {
    counts: LEVELS.map(({ level, label }) => ({ level, label, source: source.counts[level], target: target?.counts[level] ?? 0 })),
    grades,
    summary,
  }
}

export function isIdentical(diff: TreeDiff): boolean {
  return diff.summary.missing === 0 && diff.summary.changed === 0 && diff.summary.extra === 0
}
