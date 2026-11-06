// 여러 화면이 함께 쓰는 배포 도메인 조각들

import clsx from 'clsx'
import { ChevronDown, ChevronRight } from 'lucide-react'
import { useState } from 'react'
import type { Chunk, CurriculumTree, Target } from '../api/types'
import { formatNumber, formatTime } from '../lib/format'
import { chunkClass, deploymentStatusMeta } from '../lib/status'
import { systemLabel } from '../lib/targets'
import { EnvBadge, StatusBadge } from './ui'

export function DeploymentStatusBadge({ status, size }: { status: string; size?: 'sm' | 'md' }) {
  return <StatusBadge meta={deploymentStatusMeta(status)} size={size} />
}

/** "온라인캠퍼스 PROD" 처럼 시스템 이름과 환경 배지 */
export function TargetName({ target, targetId, targets, className }: { target?: Target; targetId?: number; targets?: Target[]; className?: string }) {
  const found = target ?? targets?.find((t) => t.targetId === targetId)
  if (!found) return <span className={clsx('text-slate-500', className)}>대상 #{targetId}</span>
  return (
    <span className={clsx('inline-flex items-center gap-1.5 whitespace-nowrap', className)}>
      <span>{systemLabel(found.system)}</span>
      <EnvBadge environment={found.environment} />
    </span>
  )
}

/** 청크를 칸으로 늘어놓는다. 칸에 마우스를 올리면 무엇이 들어 있는지 보인다. */
export function ChunkGrid({ chunks, selected, onSelect }: { chunks: Chunk[]; selected?: number | null; onSelect?: (offset: number) => void }) {
  return (
    <div className="flex flex-wrap gap-1">
      {chunks.map((chunk) => (
        <button
          key={chunk.chunkOffset}
          type="button"
          onClick={() => onSelect?.(chunk.chunkOffset)}
          title={`#${chunk.chunkOffset} ${chunk.label} · ${chunk.status === 'SUCCESS' ? '성공' : chunk.status === 'FAILED' ? '실패' : '대기'}${chunk.processedAt ? ` · ${formatTime(chunk.processedAt)}` : ''}${chunk.retryCount ? ` · 재전송 ${chunk.retryCount}회` : ''}`}
          className={clsx(
            'relative size-5 rounded-[4px] transition',
            chunkClass(chunk.status),
            chunk.chunkOffset === 0 && 'ring-2 ring-slate-400 ring-offset-1',
            chunk.status === 'PENDING' && chunk.retryCount > 0 && 'animate-pulse bg-amber-300',
            selected === chunk.chunkOffset && 'outline-2 outline-offset-2 outline-brand-600',
            onSelect && 'hover:scale-110',
          )}
          aria-label={`청크 ${chunk.chunkOffset}`}
        />
      ))}
    </div>
  )
}

/** 커리큘럼 트리. 학년·학기는 펼치고 접을 수 있고, 과목은 단원·차시 수만 보여 준다. */
export function CurriculumTreeView({ tree }: { tree: CurriculumTree }) {
  return (
    <ul className="space-y-1 text-sm">
      {tree.grades.map((grade, i) => (
        <GradeItem key={grade.id} grade={grade} defaultOpen={i === 0} />
      ))}
    </ul>
  )
}

function GradeItem({ grade, defaultOpen }: { grade: CurriculumTree['grades'][number]; defaultOpen: boolean }) {
  const [open, setOpen] = useState(defaultOpen)
  const subjects = grade.semesters.reduce((n, s) => n + s.subjects.length, 0)
  return (
    <li>
      <button type="button" onClick={() => setOpen(!open)} className="flex w-full items-center gap-1.5 rounded-md px-1.5 py-1 text-left hover:bg-slate-50">
        {open ? <ChevronDown className="size-4 text-slate-400" /> : <ChevronRight className="size-4 text-slate-400" />}
        <span className="font-medium">{grade.title}</span>
        <span className="text-xs text-slate-400">학기 {grade.semesters.length} · 과목 {subjects}</span>
      </button>
      {open && (
        <ul className="ml-5 space-y-2 border-l border-slate-100 py-1 pl-3">
          {grade.semesters.map((semester) => (
            <li key={semester.id}>
              <p className="text-xs font-medium text-slate-500">{semester.title}</p>
              <ul className="mt-1 grid gap-1 sm:grid-cols-2">
                {semester.subjects.map((subject) => (
                  <li key={subject.id} className="flex items-center justify-between rounded-md bg-slate-50 px-2 py-1">
                    <span>{subject.title}</span>
                    <span className="text-xs text-slate-400 tabular-nums">
                      단원 {subject.unitCount} · 차시 {formatNumber(subject.lessonCount)}
                    </span>
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      )}
    </li>
  )
}

export function LevelCountsLine({ tree }: { tree: CurriculumTree }) {
  const c = tree.counts
  return (
    <p className="text-sm text-slate-600 tabular-nums">
      학년 {c.grades} · 학기 {c.semesters} · 과목 {c.subjects} · 단원 {formatNumber(c.units)} · 차시 {formatNumber(c.lessons)}
      <span className="ml-2 text-slate-400">→ 청크 {tree.chunkCount}개</span>
    </p>
  )
}
