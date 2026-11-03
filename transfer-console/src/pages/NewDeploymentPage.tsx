import clsx from 'clsx'
import { AlertTriangle, Loader2, Search, Send, X } from 'lucide-react'
import { type FormEvent, type ReactNode, useEffect, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import type { CurriculumDeployment, CurriculumTree, Target } from '../api/types'
import { LevelCountsLine, TargetName } from '../components/domain'
import { Button, Card, Input, Mono, Notice, PageHeader, Select, Skeleton } from '../components/ui'
import { useCreateTransfer, useCurriculum, useCurriculumDeployments, useCurriculums, useSources, useTargets } from '../hooks/queries'
import { formatShort } from '../lib/format'
import { isActiveTransfer } from '../lib/status'
import { sortTargets } from '../lib/targets'
import { useCurrentUser } from '../lib/user'

export function NewDeploymentPage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const user = useCurrentUser()
  const sources = useSources()
  const targets = useTargets()
  const create = useCreateTransfer()

  const usable = sources.data?.filter((s) => s.enabled && s.sourceType === 'CURRICULUM') ?? []
  const [sourceId, setSourceId] = useState<number | null>(Number(params.get('source')) || null)
  const [curriculumId, setCurriculumId] = useState<string | null>(params.get('curriculum'))
  const [targetId, setTargetId] = useState<number | null>(Number(params.get('target')) || null)
  const [confirmed, setConfirmed] = useState(false)

  // 원본을 고르지 않았으면 첫 번째로 맞춘다
  useEffect(() => {
    if (sources.data && !usable.some((s) => s.sourceConfigId === sourceId)) setSourceId(usable[0]?.sourceConfigId ?? null)
  }, [sources.data])

  const tree = useCurriculum(sourceId, curriculumId)
  const deployments = useCurriculumDeployments(curriculumId)
  const target = targets.data?.find((t) => t.targetId === targetId) ?? null
  const isProd = target?.environment === 'PROD'

  useEffect(() => setConfirmed(false), [targetId, curriculumId])

  const blocked = (t: Target) => blockReason(t, deployments.data?.find((d) => d.targetId === t.targetId))
  const canSubmit = !!tree.data && !!target && !blocked(target) && (!isProd || confirmed) && !create.isPending

  const submit = (e: FormEvent) => {
    e.preventDefault()
    if (!canSubmit || sourceId === null || !curriculumId || !target) return
    create.mutate(
      { sourceConfigId: sourceId, sourceId: curriculumId, dataMigrationInfoId: target.targetId, transType: 'MESSAGING' },
      { onSuccess: (res) => navigate(`/deployments/${res.transferId}`) },
    )
  }

  return (
    <div className="space-y-6">
      <PageHeader title="새 배포" description="원본의 커리큘럼 하나를 대상 시스템 하나로 배포합니다." />

      <form onSubmit={submit} className="grid items-start gap-6 lg:grid-cols-3">
        <div className="space-y-6 lg:col-span-2">
          <Step no={1} title="콘텐츠" description="배포할 커리큘럼을 고릅니다.">
            <div className="grid gap-3 sm:grid-cols-[12rem_1fr]">
              <Select
                value={sourceId ?? ''}
                onChange={(e) => {
                  setSourceId(Number(e.target.value))
                  setCurriculumId(null)
                }}
                aria-label="원본 저장소"
              >
                {usable.map((s) => (
                  <option key={s.sourceConfigId} value={s.sourceConfigId}>
                    {s.name}
                  </option>
                ))}
              </Select>
              <CurriculumPicker sourceId={sourceId} value={curriculumId} tree={tree.data} onChange={setCurriculumId} />
            </div>
            {tree.isPending && curriculumId && <Skeleton className="mt-3 h-5 w-2/3" />}
            {tree.isError && <Notice tone="danger" className="mt-3">{tree.error.message}</Notice>}
            {tree.data && (
              <div className="mt-3 rounded-lg bg-slate-50 px-3 py-2.5">
                <p className="text-sm font-medium">
                  <Mono>{tree.data.curriculumId}</Mono> {tree.data.title}
                </p>
                <LevelCountsLine tree={tree.data} />
              </div>
            )}
          </Step>

          <Step no={2} title="대상" description="배포할 시스템을 하나 고릅니다. 여러 곳에 한 번에 보내는 기능은 아직 없습니다.">
            {targets.isPending ? (
              <Skeleton className="h-40" />
            ) : (
              <div role="radiogroup" className="space-y-2">
                {sortTargets(targets.data ?? []).map((t) => (
                  <TargetOption
                    key={t.targetId}
                    target={t}
                    deployment={deployments.data?.find((d) => d.targetId === t.targetId)}
                    reason={curriculumId ? blocked(t) : null}
                    selected={t.targetId === targetId}
                    onSelect={() => setTargetId(t.targetId)}
                  />
                ))}
              </div>
            )}
          </Step>
        </div>

        <Card className="lg:sticky lg:top-24">
          <div className="border-b border-slate-100 px-5 py-4">
            <p className="text-xs font-medium text-slate-400">3</p>
            <h2 className="text-[15px] font-semibold">확인</h2>
          </div>
          <div className="space-y-4 p-5 text-sm">
            <Summary label="콘텐츠">{tree.data ? <><Mono>{tree.data.curriculumId}</Mono> {tree.data.title}</> : <span className="text-slate-400">고르지 않음</span>}</Summary>
            <Summary label="대상">{target ? <TargetName target={target} /> : <span className="text-slate-400">고르지 않음</span>}</Summary>
            <Summary label="청크">{tree.data ? `${tree.data.chunkCount}개 (뼈대 1 + 과목 ${tree.data.counts.subjects})` : '—'}</Summary>
            <Summary label="요청자">{user}</Summary>

            {target && tree.data && deployments.data?.find((d) => d.targetId === target.targetId)?.lastSucceededAt && (
              <Notice tone="info">이미 배포된 대상입니다. 같은 ID 의 데이터는 새 내용으로 덮어씁니다.</Notice>
            )}

            {isProd && (
              <label className="flex items-start gap-2 rounded-lg border border-rose-200 bg-rose-50 p-3 text-rose-900">
                <input type="checkbox" checked={confirmed} onChange={(e) => setConfirmed(e.target.checked)} className="mt-0.5 size-4 accent-rose-600" />
                <span>
                  <span className="font-medium">운영 환경에 배포하는 것을 확인했습니다.</span>
                  <br />
                  <span className="text-xs opacity-80">학습자가 쓰는 데이터가 바로 바뀝니다.</span>
                </span>
              </label>
            )}

            {create.isError && <Notice tone="danger" title="배포를 요청하지 못했습니다">{create.error.message}</Notice>}

            <div className="flex gap-2 pt-1">
              <Button variant="secondary" onClick={() => navigate(-1)} className="flex-1">
                취소
              </Button>
              <Button type="submit" disabled={!canSubmit} className="flex-[2]">
                {create.isPending ? <Loader2 className="size-4 animate-spin" /> : <Send className="size-4" />}
                배포 요청
              </Button>
            </div>
          </div>
        </Card>
      </form>
    </div>
  )
}

/** 이 대상을 고를 수 없는 이유. 고를 수 있으면 null */
function blockReason(target: Target, deployment: CurriculumDeployment | undefined): ReactNode {
  if (!target.enabled) return '배포 허용이 꺼져 있습니다.'
  if (deployment?.last && isActiveTransfer(deployment.last.status)) {
    return (
      <>
        같은 커리큘럼을 보내는 배포가 진행 중입니다.{' '}
        <Link to={`/deployments/${deployment.last.transferId}`} className="font-medium underline">
          진행 중인 배포 보기
        </Link>
      </>
    )
  }
  return null
}

function Step({ no, title, description, children }: { no: number; title: string; description: string; children: ReactNode }) {
  return (
    <Card>
      <div className="border-b border-slate-100 px-5 py-4">
        <p className="text-xs font-medium text-slate-400">{no}</p>
        <h2 className="text-[15px] font-semibold">{title}</h2>
        <p className="mt-0.5 text-sm text-slate-500">{description}</p>
      </div>
      <div className="p-5">{children}</div>
    </Card>
  )
}

function Summary({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="flex gap-3">
      <span className="w-14 shrink-0 text-slate-500">{label}</span>
      <span className="min-w-0 text-slate-900">{children}</span>
    </div>
  )
}

function TargetOption({ target, deployment, reason, selected, onSelect }: {
  target: Target
  deployment: CurriculumDeployment | undefined
  reason: ReactNode
  selected: boolean
  onSelect: () => void
}) {
  const disabled = reason !== null
  return (
    <label
      className={clsx(
        'flex items-start gap-3 rounded-lg border px-4 py-3 transition',
        disabled ? 'cursor-not-allowed border-slate-200 bg-slate-50 text-slate-400' : 'cursor-pointer hover:border-slate-300',
        selected && !disabled && 'border-brand-500 bg-brand-50/50 ring-1 ring-brand-500',
      )}
    >
      <input type="radio" name="target" checked={selected && !disabled} disabled={disabled} onChange={onSelect} className="mt-1 accent-brand-600" />
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-x-3 gap-y-1">
          <TargetName target={target} className={clsx('font-medium', !disabled && 'text-slate-900')} />
          {target.lastCheck && !target.lastCheck.ok && <span className="text-xs text-rose-600">● 연결 끊김</span>}
        </div>
        <p className="mt-0.5 text-xs">
          {reason ? (
            <span className="inline-flex items-center gap-1 text-slate-500">
              <AlertTriangle className="size-3" /> {reason}
            </span>
          ) : deployment?.lastSucceededAt ? (
            <span className="text-slate-500">이미 배포됨 ({formatShort(deployment.lastSucceededAt)}) → 덮어씀</span>
          ) : (
            <span className="text-slate-500">아직 배포 안 됨</span>
          )}
          {target.environment === 'PROD' && !reason && <span className="ml-2 font-medium text-rose-600">운영 환경</span>}
        </p>
      </div>
    </label>
  )
}

/** 커리큘럼 검색 상자. 고른 뒤에는 선택한 것을 보여 주고 X 로 지운다. */
function CurriculumPicker({ sourceId, value, tree, onChange }: { sourceId: number | null; value: string | null; tree: CurriculumTree | undefined; onChange: (id: string | null) => void }) {
  const [keyword, setKeyword] = useState('')
  const [q, setQ] = useState('')
  const [open, setOpen] = useState(false)
  useEffect(() => {
    const id = setTimeout(() => setQ(keyword), 200)
    return () => clearTimeout(id)
  }, [keyword])
  const results = useCurriculums(open ? sourceId : null, { q, size: 8 })

  if (value) {
    return (
      <div className="flex items-center justify-between rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm shadow-sm">
        <span className="truncate">
          <Mono className="font-medium">{value}</Mono> <span className="text-slate-600">{tree?.title}</span>
        </span>
        <button type="button" onClick={() => onChange(null)} className="text-slate-400 hover:text-slate-700" aria-label="커리큘럼 다시 고르기">
          <X className="size-4" />
        </button>
      </div>
    )
  }

  return (
    <div className="relative">
      <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-slate-400" />
      <Input
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        onFocus={() => setOpen(true)}
        onBlur={() => setTimeout(() => setOpen(false), 150)}
        placeholder="커리큘럼 ID 또는 제목으로 검색"
        className="pl-9"
        aria-label="커리큘럼 검색"
      />
      {open && (
        <div className="absolute inset-x-0 top-full z-10 mt-1 max-h-72 overflow-y-auto rounded-lg border border-slate-200 bg-white py-1 shadow-lg">
          {results.isPending ? (
            <p className="px-3 py-2 text-sm text-slate-400">찾는 중…</p>
          ) : results.isError ? (
            <p className="px-3 py-2 text-sm text-rose-600">{results.error.message}</p>
          ) : results.data.items.length === 0 ? (
            <p className="px-3 py-2 text-sm text-slate-400">맞는 커리큘럼이 없습니다.</p>
          ) : (
            results.data.items.map((c) => (
              <button
                key={c.curriculumId}
                type="button"
                onMouseDown={(e) => e.preventDefault()}
                onClick={() => {
                  onChange(c.curriculumId)
                  setOpen(false)
                  setKeyword('')
                }}
                className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm hover:bg-slate-50"
              >
                <Mono className="font-medium">{c.curriculumId}</Mono>
                <span className="truncate text-slate-600">{c.title}</span>
                <span className="ml-auto text-xs text-slate-400">과목 {c.subjectCount}</span>
              </button>
            ))
          )}
        </div>
      )}
    </div>
  )
}
