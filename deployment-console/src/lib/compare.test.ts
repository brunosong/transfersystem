import { describe, expect, it } from 'vitest'
import { buildCurriculum, countLevels } from '../api/mock/content'
import type { CurriculumTree } from '../api/types'
import { compareTrees, isIdentical } from './compare'

const source = buildCurriculum('CUR002')

function without(tree: CurriculumTree, subjectId: string): CurriculumTree {
  const grades = tree.grades.map((g) => ({
    ...g,
    semesters: g.semesters.map((s) => ({ ...s, subjects: s.subjects.filter((x) => x.id !== subjectId) })),
  }))
  return { ...tree, grades, counts: countLevels(grades) }
}

describe('compareTrees', () => {
  it('같은 트리는 차이가 없다', () => {
    const diff = compareTrees(source, buildCurriculum('CUR002'))
    expect(isIdentical(diff)).toBe(true)
    expect(diff.summary.same).toBe(40)
    expect(diff.counts.every((c) => c.source === c.target)).toBe(true)
  })

  it('대상에 빠진 과목을 찾는다', () => {
    const missingId = source.grades[0].semesters[0].subjects[2].id
    const diff = compareTrees(source, without(source, missingId))
    expect(diff.summary.missing).toBe(1)
    expect(diff.grades[0].semesters[0].subjects[2]).toMatchObject({ id: missingId, kind: 'missing', target: null })
    expect(diff.counts.find((c) => c.level === 'subjects')).toMatchObject({ source: 40, target: 39 })
  })

  it('배포 뒤 원본이 고쳐지면 변경됨으로 본다', () => {
    const revised = buildCurriculum('CUR002', true)
    const diff = compareTrees(revised, source)
    expect(diff.summary.changed).toBe(1)
    const changed = diff.grades[0].semesters[0].subjects.filter((s) => s.kind === 'changed').map((s) => s.source?.title)
    expect(changed).toContain('수학 (개정)')
  })

  it('원본에서 지워진 과목은 원본에 없음으로 본다', () => {
    const removedId = source.grades[1].semesters[1].subjects[4].id
    const diff = compareTrees(without(source, removedId), source)
    expect(diff.summary.extra).toBe(1)
  })

  it('대상에 아무것도 없으면 전부 빠졌다', () => {
    const diff = compareTrees(source, null)
    expect(diff.summary.missing).toBe(40)
    expect(diff.counts.every((c) => c.target === 0)).toBe(true)
  })
})
