import clsx from 'clsx'
import { ArrowRight, Database, Loader2, Send } from 'lucide-react'
import { type FormEvent, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'
import type { SourceConfig } from '../api/types'
import { Button, Card, CardHeader, ErrorState, Mono, Skeleton } from '../components/ui'
import { useCreateTransfer, useSourceConfigs } from '../hooks/queries'
import { systemLabel, TARGETS } from '../lib/targets'

/** transfer-service 가 원본 데이터를 읽을 수 있는 유형(SourceType) */
const SUPPORTED_SOURCE_TYPES = ['CURRICULUM', 'EXAM_RESULT_DATA']

export function NewTransferPage() {
  const navigate = useNavigate()
  const configs = useSourceConfigs()
  const create = useCreateTransfer()

  const [sourceConfigId, setSourceConfigId] = useState<number | null>(null)
  const [sourceId, setSourceId] = useState('')
  const [targetId, setTargetId] = useState(TARGETS[0].id)
  const [touched, setTouched] = useState(false)

  // 처음 들어오면 고를 수 있는 첫 원본을 미리 골라 둔다
  useEffect(() => {
    if (sourceConfigId === null && configs.data) {
      const first = configs.data.find((c) => SUPPORTED_SOURCE_TYPES.includes(c.sourceType))
      if (first) setSourceConfigId(first.sourceConfigId)
    }
  }, [configs.data, sourceConfigId])

  const sourceIdError = touched && sourceId.trim() === '' ? '원본 ID를 입력하세요.' : null
  const canSubmit = sourceConfigId !== null && sourceId.trim() !== '' && !create.isPending

  const submit = (e: FormEvent) => {
    e.preventDefault()
    setTouched(true)
    if (!canSubmit || sourceConfigId === null) return
    create.mutate(
      { sourceConfigId, sourceId: sourceId.trim(), dataMigrationInfoId: targetId, transType: 'MESSAGING' },
      { onSuccess: (res) => navigate(`/transfers/${res.transferId}`) },
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">새 전송</h1>
        <p className="mt-1 text-sm text-slate-500">원본 저장소의 데이터 하나를 골라 대상 시스템으로 이관합니다.</p>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <form onSubmit={submit} className="space-y-6 lg:col-span-2">
          <Card>
            <CardHeader title="1. 원본 저장소" description="데이터를 읽어 올 곳을 고릅니다." />
            <div className="p-5">
              {configs.isPending ? (
                <div className="grid gap-3 sm:grid-cols-2">
                  {Array.from({ length: 4 }, (_, i) => (
                    <Skeleton key={i} className="h-24" />
                  ))}
                </div>
              ) : configs.isError ? (
                <ErrorState error={configs.error} onRetry={() => void configs.refetch()} />
              ) : (
                <div role="radiogroup" className="grid gap-3 sm:grid-cols-2">
                  {configs.data.map((config) => (
                    <SourceOption
                      key={config.sourceConfigId}
                      config={config}
                      selected={config.sourceConfigId === sourceConfigId}
                      disabled={!SUPPORTED_SOURCE_TYPES.includes(config.sourceType)}
                      onSelect={() => setSourceConfigId(config.sourceConfigId)}
                    />
                  ))}
                </div>
              )}
            </div>
          </Card>

          <Card>
            <CardHeader title="2. 원본 ID" description="이관할 문서(레코드)의 ID입니다. 커리큘럼이면 curriculumId 를 넣습니다." />
            <div className="p-5">
              <input
                value={sourceId}
                onChange={(e) => setSourceId(e.target.value)}
                onBlur={() => setTouched(true)}
                placeholder="예: curriculum-2026-math-g3"
                aria-invalid={sourceIdError !== null}
                className={clsx(
                  'w-full rounded-lg border bg-white px-3.5 py-2.5 font-mono text-sm shadow-sm transition outline-none',
                  'placeholder:font-sans placeholder:text-slate-400 focus:ring-4',
                  sourceIdError
                    ? 'border-rose-300 focus:border-rose-400 focus:ring-rose-100'
                    : 'border-slate-200 focus:border-brand-500 focus:ring-brand-100',
                )}
              />
              {sourceIdError ? (
                <p className="mt-1.5 text-xs text-rose-600">{sourceIdError}</p>
              ) : (
                api.mode === 'mock' && (
                  <p className="mt-1.5 text-xs text-slate-500">
                    모의 모드: ID에 <code className="font-mono text-slate-700">fail</code>을 넣으면 부트캠프에서 실패하고 되돌리는 흐름을 볼
                    수 있습니다.
                  </p>
                )
              )}
            </div>
          </Card>

          <Card>
            <CardHeader title="3. 대상 시스템" description="데이터를 반영할 곳입니다." />
            <div role="radiogroup" className="grid gap-3 p-5 sm:grid-cols-2">
              {TARGETS.map((target) => (
                <button
                  key={target.id}
                  type="button"
                  role="radio"
                  aria-checked={target.id === targetId}
                  onClick={() => setTargetId(target.id)}
                  className={clsx(
                    'flex items-center justify-between rounded-lg border px-4 py-3 text-left text-sm transition',
                    target.id === targetId
                      ? 'border-brand-500 bg-brand-50/60 ring-1 ring-brand-500'
                      : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50',
                  )}
                >
                  <span className="font-medium">{systemLabel(target.system)}</span>
                  <span
                    className={clsx(
                      'rounded px-1.5 py-0.5 text-[11px] font-semibold',
                      target.environment === 'PROD' ? 'bg-rose-50 text-rose-700' : 'bg-slate-100 text-slate-600',
                    )}
                  >
                    {target.environment}
                  </span>
                </button>
              ))}
            </div>
          </Card>

          {create.isError && (
            <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800">
              전송을 요청하지 못했습니다. {create.error.message}
            </div>
          )}

          <div className="flex justify-end gap-2">
            <Button type="button" variant="ghost" onClick={() => navigate('/transfers')}>
              취소
            </Button>
            <Button type="submit" disabled={!canSubmit}>
              {create.isPending ? <Loader2 className="size-4 animate-spin" /> : <Send className="size-4" />}
              전송 요청
            </Button>
          </div>
        </form>

        <FlowGuide />
      </div>
    </div>
  )
}

function SourceOption({ config, selected, disabled, onSelect }: {
  config: SourceConfig
  selected: boolean
  disabled: boolean
  onSelect: () => void
}) {
  return (
    <button
      type="button"
      role="radio"
      aria-checked={selected}
      disabled={disabled}
      onClick={onSelect}
      className={clsx(
        'rounded-lg border p-4 text-left transition',
        disabled && 'cursor-not-allowed border-dashed border-slate-200 bg-slate-50/60 opacity-60',
        !disabled && (selected ? 'border-brand-500 bg-brand-50/60 ring-1 ring-brand-500' : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'),
      )}
    >
      <div className="flex items-center justify-between gap-2">
        <span className="rounded bg-slate-900/5 px-1.5 py-0.5 text-[11px] font-semibold text-slate-700">{config.sourceType}</span>
        <span className="inline-flex items-center gap-1 text-xs text-slate-500">
          <Database className="size-3.5" />
          {config.dbType}
        </span>
      </div>
      <p className="mt-3 truncate text-sm font-medium text-slate-900">
        {config.database}.{config.tableOrCollection}
      </p>
      <Mono className="mt-0.5 block truncate text-slate-500">
        {config.host}:{config.port}
      </Mono>
      {disabled && <p className="mt-2 text-xs text-slate-500">아직 지원하지 않는 유형입니다</p>}
    </button>
  )
}

function FlowGuide() {
  const steps = [
    ['접수', '원본에서 데이터를 읽어 청크로 나눕니다.'],
    ['아웃박스 저장', '전송 기록과 보낼 메시지를 한 트랜잭션으로 저장합니다.'],
    ['Kafka 발행', '스케줄러가 아웃박스의 메시지를 data-migration 으로 보냅니다.'],
    ['대상 반영', '대상 DB에 청크를 반영하고 결과를 돌려줍니다.'],
    ['결과 집계', '청크 결과를 모아 전송 상태를 갱신합니다.'],
  ]
  return (
    <Card className="h-fit lg:sticky lg:top-24">
      <CardHeader title="요청 후에 일어나는 일" />
      <ol className="space-y-4 p-5">
        {steps.map(([title, body], i) => (
          <li key={title} className="flex gap-3">
            <span className="grid size-6 shrink-0 place-items-center rounded-full bg-brand-50 text-xs font-semibold text-brand-700">
              {i + 1}
            </span>
            <div>
              <p className="text-sm font-medium text-slate-900">{title}</p>
              <p className="mt-0.5 text-xs leading-relaxed text-slate-500">{body}</p>
            </div>
          </li>
        ))}
      </ol>
      <p className="flex items-center gap-1.5 border-t border-slate-100 px-5 py-3 text-xs text-slate-500">
        <ArrowRight className="size-3.5" />
        요청하면 진행 화면으로 바로 이동합니다
      </p>
    </Card>
  )
}
