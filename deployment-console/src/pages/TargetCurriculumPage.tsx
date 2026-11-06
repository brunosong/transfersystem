import clsx from 'clsx'
import { ArrowLeft, CheckCircle2, Send } from 'lucide-react'
import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { isNotFound } from '../api'
import type { CurriculumTree } from '../api/types'
import { TargetName, DeploymentStatusBadge } from '../components/domain'
import { Button, Card, CardHeader, EmptyState, ErrorState, Mono, Notice, Skeleton, Toggle } from '../components/ui'
import { useCurriculum, useCurriculumDeployments, useSources, useTargetCurriculum, useTargets } from '../hooks/queries'
import { compareTrees, type DiffKind, isIdentical, type SubjectDiff, type TreeDiff } from '../lib/compare'
import { formatNumber, formatShort } from '../lib/format'

export function TargetCurriculumPage() {
  const params = useParams()
  const targetId = Number(params.targetId)
  const curriculumId = params.curriculumId ?? ''

  const targets = useTargets()
  const sources = useSources()
  const targetTree = useTargetCurriculum(targetId, curriculumId)
  const deployments = useCurriculumDeployments(curriculumId)
  const last = deployments.data?.find((d) => d.targetId === targetId)?.last ?? null

  // 대상에 마지막으로 들어온 원본과 비교한다. 대상에 아직 없으면 사용할 수 있는 첫 원본과 비교한다.
  const fallbackSource = sources.data?.find((s) => s.enabled && s.sourceType === 'CURRICULUM')?.sourceConfigId ?? null
  const sourceId = targetTree.data?.sourceConfigId ?? (targetTree.isError ? fallbackSource : null)
  const sourceTree = useCurriculum(sourceId, curriculumId)
  const source = sources.data?.find((s) => s.sourceConfigId === sourceId)

  const absent = targetTree.isError && isNotFound(targetTree.error)
  const loading = targetTree.isPending || (absent && sources.isPending) || (sourceId !== null && sourceTree.isPending)

  return (
    <div className="space-y-6">
      <div>
        <Link to="/settings/targets" className="inline-flex items-center gap-1 text-sm text-slate-500 hover:text-slate-800">
          <ArrowLeft className="size-4" /> 대상 시스템
        </Link>
        <div className="mt-2 flex flex-wrap items-center gap-x-3 gap-y-2">
          <h1 className="text-2xl font-semibold tracking-tight">
            <Mono className="text-2xl">{curriculumId}</Mono>{' '}
            <span className="font-medium text-slate-600">{targetTree.data?.title ?? sourceTree.data?.title}</span>
          </h1>
          <span className="text-slate-400">대상</span>
          <TargetName targetId={targetId} targets={targets.data} className="text-lg font-medium" />
        </div>
        <p className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-slate-500">
          {last ? (
            <Link to={`/deployments/${last.deploymentId}`} className="inline-flex items-center gap-2 hover:underline">
              마지막 배포 {formatShort(last.at)} <DeploymentStatusBadge status={last.status} size="sm" />
            </Link>
          ) : (
            <span>이 대상으로 배포한 적이 없습니다</span>
          )}
          {targetTree.data && <span>대상 데이터 갱신 {formatShort(targetTree.data.updatedAt)}</span>}
          {source && <span>비교 원본 {source.name}</span>}
        </p>
      </div>

      {loading ? (
        <div className="space-y-4">
          <Skeleton className="h-44" />
          <Skeleton className="h-64" />
        </div>
      ) : targetTree.isError && !absent ? (
        <Card>
          <ErrorState error={targetTree.error} onRetry={() => void targetTree.refetch()} />
        </Card>
      ) : sourceTree.isError ? (
        <Card>
          {isNotFound(sourceTree.error) ? (
            <EmptyState title="원본에서 이 커리큘럼을 찾을 수 없습니다" description="원본에서 지워졌거나 비교할 원본 저장소가 없습니다." />
          ) : (
            <ErrorState error={sourceTree.error} onRetry={() => void sourceTree.refetch()} />
          )}
        </Card>
      ) : sourceTree.data ? (
        <Comparison source={sourceTree.data} target={absent ? null : (targetTree.data ?? null)} redeployHref={`/deployments/new?source=${sourceId}&curriculum=${encodeURIComponent(curriculumId)}&target=${targetId}`} />
      ) : (
        <Card>
          <EmptyState title="비교할 원본이 없습니다" description="사용할 수 있는 원본 저장소가 없습니다." />
        </Card>
      )}
    </div>
  )
}

function Comparison({ source, target, redeployHref }: { source: CurriculumTree; target: CurriculumTree | null; redeployHref: string }) {
  const diff = compareTrees(source, target)
  const identical = target !== null && isIdentical(diff)
  const [onlyDiff, setOnlyDiff] = useState(!identical)

  return (
    <>
      {target === null ? (
        <Notice tone="warning" title="대상에 이 커리큘럼이 없습니다">아직 배포하지 않았거나 모든 청크가 실패했습니다.</Notice>
      ) : identical ? (
        <Notice tone="success" title="원본과 같습니다">학년부터 차시까지 개수와 과목 내용이 모두 일치합니다.</Notice>
      ) : (
        <Notice tone="warning" title="원본과 다릅니다">
          {[
            diff.summary.missing && `대상에 없는 과목 ${diff.summary.missing}개`,
            diff.summary.changed && `내용이 다른 과목 ${diff.summary.changed}개`,
            diff.summary.extra && `원본에 없는 과목 ${diff.summary.extra}개`,
          ]
            .filter(Boolean)
            .join(' · ')}
          . 다시 배포하면 원본 내용으로 맞춰집니다.
        </Notice>
      )}

      <div className="grid items-start gap-6 lg:grid-cols-[18rem_1fr]">
        <Card>
          <CardHeader title="개수 비교" />
          <table className="w-full text-sm">
            <thead>
              <tr className="text-xs text-slate-500">
                <th className="px-5 py-2 text-left font-medium" />
                <th className="px-2 py-2 text-right font-medium">원본</th>
                <th className="px-2 py-2 text-right font-medium">대상</th>
                <th className="px-5 py-2 text-right font-medium">차이</th>
              </tr>
            </thead>
            <tbody>
              {diff.counts.map((row) => {
                const delta = row.target - row.source
                return (
                  <tr key={row.level} className="border-t border-slate-100">
                    <td className="px-5 py-2 text-slate-600">{row.label}</td>
                    <td className="px-2 py-2 text-right tabular-nums">{formatNumber(row.source)}</td>
                    <td className="px-2 py-2 text-right tabular-nums">{formatNumber(row.target)}</td>
                    <td className={clsx('px-5 py-2 text-right font-medium tabular-nums', delta === 0 ? 'text-slate-300' : 'text-amber-700')}>
                      {delta === 0 ? '—' : `${delta > 0 ? '+' : ''}${formatNumber(delta)}`}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
          <div className="border-t border-slate-100 p-4">
            <Link to={redeployHref}>
              <Button variant={identical ? 'secondary' : 'primary'} className="w-full">
                <Send className="size-4" />이 커리큘럼 다시 배포
              </Button>
            </Link>
          </div>
        </Card>

        <Card>
          <CardHeader
            title="과목별 비교"
            description="ID 기준으로 비교합니다. 같은 ID 인데 제목이나 단원·차시 수가 다르면 변경됨으로 표시합니다."
            action={
              <label className="flex shrink-0 items-center gap-2 text-sm text-slate-600">
                차이만 보기 <Toggle checked={onlyDiff} onChange={setOnlyDiff} label="차이만 보기" />
              </label>
            }
          />
          <SubjectDiffList diff={diff} onlyDiff={onlyDiff} />
        </Card>
      </div>
    </>
  )
}

const KIND_META: Record<DiffKind, { label: string; className: string }> = {
  same: { label: '같음', className: 'border-slate-200 bg-white text-slate-700' },
  missing: { label: '대상에 없음', className: 'border-rose-200 bg-rose-50 text-rose-800' },
  changed: { label: '변경됨', className: 'border-amber-200 bg-amber-50 text-amber-900' },
  extra: { label: '원본에 없음', className: 'border-violet-200 bg-violet-50 text-violet-800' },
}

function SubjectDiffList({ diff, onlyDiff }: { diff: TreeDiff; onlyDiff: boolean }) {
  const show = (s: SubjectDiff) => !onlyDiff || s.kind !== 'same'
  const grades = diff.grades
    .map((g) => ({ ...g, semesters: g.semesters.map((s) => ({ ...s, subjects: s.subjects.filter(show) })).filter((s) => s.subjects.length > 0) }))
    .filter((g) => g.semesters.length > 0)

  if (grades.length === 0) {
    return (
      <div className="flex items-center gap-2 px-5 py-8 text-sm text-slate-500">
        <CheckCircle2 className="size-4 text-emerald-500" /> 다른 과목이 없습니다.
      </div>
    )
  }

  return (
    <div className="space-y-4 p-5">
      {grades.map((grade) => (
        <section key={grade.id}>
          <h3 className="text-sm font-semibold">{grade.title}</h3>
          <div className="mt-2 space-y-3 border-l border-slate-100 pl-4">
            {grade.semesters.map((semester) => (
              <div key={semester.id}>
                <p className="mb-1.5 text-xs font-medium text-slate-500">{semester.title}</p>
                <div className="grid gap-2 sm:grid-cols-2 xl:grid-cols-3">
                  {semester.subjects.map((s) => (
                    <SubjectCard key={s.id} diff={s} />
                  ))}
                </div>
              </div>
            ))}
          </div>
        </section>
      ))}
    </div>
  )
}

function SubjectCard({ diff }: { diff: SubjectDiff }) {
  const meta = KIND_META[diff.kind]
  const node = diff.source ?? diff.target!
  return (
    <div className={clsx('rounded-lg border px-3 py-2 text-sm', meta.className)}>
      <div className="flex items-center justify-between gap-2">
        <span className="font-medium">{node.title}</span>
        <span className="text-[11px] font-semibold">{meta.label}</span>
      </div>
      <Mono className="text-[11px] opacity-60">{diff.id}</Mono>
      {diff.kind === 'changed' && diff.source && diff.target && (
        <dl className="mt-1 space-y-0.5 text-xs">
          {diff.source.title !== diff.target.title && (
            <div>
              제목: <span className="line-through opacity-60">{diff.target.title}</span> → {diff.source.title}
            </div>
          )}
          {diff.source.unitCount !== diff.target.unitCount && (
            <div>
              단원: {diff.target.unitCount} → {diff.source.unitCount}
            </div>
          )}
          {diff.source.lessonCount !== diff.target.lessonCount && (
            <div>
              차시: {diff.target.lessonCount} → {diff.source.lessonCount}
            </div>
          )}
        </dl>
      )}
    </div>
  )
}
