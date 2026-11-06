import clsx from 'clsx'
import { ChevronDown, ChevronRight, Loader2, Plug } from 'lucide-react'
import { Fragment, useState } from 'react'
import { Link } from 'react-router-dom'
import type { Target } from '../api/types'
import { DeploymentStatusBadge } from '../components/domain'
import { Button, Card, ConnectionStatus, EnvBadge, ErrorState, Mono, Notice, PageHeader, Skeleton, Table, Td, Th, Toggle } from '../components/ui'
import { useSetTargetEnabled, useTargetCurriculums, useTargets, useTestTarget, useDeployments } from '../hooks/queries'
import { formatRelative } from '../lib/format'
import { sortTargets, systemLabel, targetLabel } from '../lib/targets'

export function TargetsPage() {
  const targets = useTargets()
  const [open, setOpen] = useState<number | null>(null)

  return (
    <div className="space-y-6">
      <PageHeader title="대상 시스템" description="배포할 수 있는 서비스 시스템과 그 상태입니다. 행을 누르면 최근 배포와 반영된 커리큘럼이 보입니다." />

      <Notice>
        접속 정보는 target-agent 설정 파일에서 관리합니다. 여기서는 연결을 확인하고 배포 허용 여부만 바꿉니다.
      </Notice>

      <Card>
        {targets.isPending ? (
          <div className="space-y-3 p-5">
            {Array.from({ length: 4 }, (_, i) => (
              <Skeleton key={i} className="h-10" />
            ))}
          </div>
        ) : targets.isError ? (
          <ErrorState error={targets.error} onRetry={() => void targets.refetch()} />
        ) : (
          <Table>
            <thead>
              <tr>
                <Th className="w-8" />
                <Th>시스템</Th>
                <Th>환경</Th>
                <Th>DB</Th>
                <Th>연결</Th>
                <Th>배포 허용</Th>
                <Th />
              </tr>
            </thead>
            <tbody>
              {sortTargets(targets.data).map((target) => (
                <TargetRow
                  key={target.targetId}
                  target={target}
                  open={open === target.targetId}
                  onToggleOpen={() => setOpen(open === target.targetId ? null : target.targetId)}
                />
              ))}
            </tbody>
          </Table>
        )}
      </Card>
    </div>
  )
}

function TargetRow({ target, open, onToggleOpen }: { target: Target; open: boolean; onToggleOpen: () => void }) {
  const test = useTestTarget()
  const setEnabled = useSetTargetEnabled()
  const check = test.data ?? target.lastCheck

  const toggle = (enabled: boolean) => {
    if (!enabled && !window.confirm(`${targetLabel(target)} 으로의 배포를 막을까요? 진행 중인 배포는 그대로 진행됩니다.`)) return
    setEnabled.mutate({ targetId: target.targetId, enabled })
  }

  return (
    <Fragment>
      <tr className={clsx('cursor-pointer transition hover:bg-slate-50', open && 'bg-slate-50')} onClick={onToggleOpen}>
        <Td className="pr-0 text-slate-400">{open ? <ChevronDown className="size-4" /> : <ChevronRight className="size-4" />}</Td>
        <Td className="font-medium text-slate-900">{systemLabel(target.system)}</Td>
        <Td>
          <EnvBadge environment={target.environment} />
        </Td>
        <Td>
          <Mono className="text-slate-600">{target.dbUrl}</Mono>
        </Td>
        <Td>
          <ConnectionStatus check={check} />
          {check && !check.ok && <p className="mt-1 max-w-56 text-xs text-rose-600">{check.message}</p>}
        </Td>
        <Td>
          <div onClick={(e) => e.stopPropagation()} className="flex items-center gap-2">
            <Toggle checked={target.enabled} onChange={toggle} disabled={setEnabled.isPending} label={`${targetLabel(target)} 배포 허용`} />
            <span className={clsx('text-xs', target.enabled ? 'text-slate-600' : 'text-slate-400')}>{target.enabled ? '켜짐' : '꺼짐'}</span>
          </div>
        </Td>
        <Td className="text-right">
          <Button
            variant="ghost"
            className="px-2"
            title="연결 테스트"
            disabled={test.isPending}
            onClick={(e) => {
              e.stopPropagation()
              test.mutate(target.targetId)
            }}
          >
            {test.isPending ? <Loader2 className="size-4 animate-spin" /> : <Plug className="size-4" />}
          </Button>
        </Td>
      </tr>
      {open && (
        <tr>
          <td colSpan={7} className="border-b border-slate-100 bg-slate-50/60 px-6 py-5">
            <TargetDetail target={target} />
          </td>
        </tr>
      )}
    </Fragment>
  )
}

function TargetDetail({ target }: { target: Target }) {
  const recent = useDeployments({ targetId: target.targetId, size: 5 })
  const curriculums = useTargetCurriculums(target.targetId)

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <section>
        <div className="mb-2 flex items-center justify-between">
          <h3 className="text-sm font-semibold">최근 배포</h3>
          <Link to={`/deployments?target=${target.targetId}`} className="text-xs font-medium text-brand-700 hover:underline">
            전체 보기
          </Link>
        </div>
        {recent.isPending ? (
          <Skeleton className="h-24" />
        ) : recent.isError ? (
          <p className="text-sm text-rose-600">{recent.error.message}</p>
        ) : recent.data.items.length === 0 ? (
          <p className="text-sm text-slate-500">이 대상으로 배포한 적이 없습니다.</p>
        ) : (
          <ul className="divide-y divide-slate-100 rounded-lg border border-slate-200 bg-white">
            {recent.data.items.map((t) => (
              <li key={t.deploymentId}>
                <Link to={`/deployments/${t.deploymentId}`} className="flex items-center gap-3 px-3 py-2 text-sm hover:bg-slate-50">
                  <DeploymentStatusBadge status={t.status} size="sm" />
                  <Mono className="font-medium">{t.sourceId}</Mono>
                  <span className="truncate text-slate-500">{t.sourceTitle}</span>
                  <span className="ml-auto shrink-0 text-xs text-slate-400">{formatRelative(t.createdAt)}</span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section>
        <h3 className="mb-2 text-sm font-semibold">
          반영된 커리큘럼 {curriculums.data && <span className="font-normal text-slate-400">{curriculums.data.length}개</span>}
        </h3>
        {curriculums.isPending ? (
          <Skeleton className="h-24" />
        ) : curriculums.isError ? (
          <p className="text-sm text-rose-600">{curriculums.error.message}</p>
        ) : curriculums.data.length === 0 ? (
          <p className="text-sm text-slate-500">아직 들어간 커리큘럼이 없습니다.</p>
        ) : (
          <ul className="max-h-60 divide-y divide-slate-100 overflow-y-auto rounded-lg border border-slate-200 bg-white">
            {curriculums.data.map((c) => (
              <li key={c.curriculumId}>
                <Link
                  to={`/targets/${target.targetId}/curriculums/${encodeURIComponent(c.curriculumId)}`}
                  className="flex items-center gap-3 px-3 py-2 text-sm hover:bg-slate-50"
                >
                  <Mono className="font-medium">{c.curriculumId}</Mono>
                  <span className="truncate text-slate-600">{c.title}</span>
                  <span className="ml-auto shrink-0 text-xs text-slate-400">
                    과목 {c.subjectCount} · {formatRelative(c.updatedAt)}
                  </span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}
