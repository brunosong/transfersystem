import { Database, Loader2, Pencil, Plug, Plus, Trash2 } from 'lucide-react'
import { type FormEvent, useCallback, useState } from 'react'
import type { ConnectionCheck, DbType, SourceConfig, SourceConfigInput, SourceType } from '../api/types'
import {
  Button,
  Card,
  ConnectionStatus,
  EmptyState,
  ErrorState,
  Field,
  Input,
  Mono,
  Notice,
  PageHeader,
  Select,
  SidePanel,
  Skeleton,
  Table,
  Td,
  Th,
  Toggle,
} from '../components/ui'
import { useDeleteSource, useSaveSource, useSources, useTestSource } from '../hooks/queries'
import { formatNumber } from '../lib/format'

const SOURCE_TYPE_LABELS: Record<SourceType, string> = { CURRICULUM: '커리큘럼', EXAM_RESULT_DATA: '시험 결과' }
const DEFAULT_PORT: Record<DbType, number> = { MONGO: 27017, MYSQL: 3306 }

type Editing = { mode: 'create' } | { mode: 'edit'; source: SourceConfig } | null

export function SourcesPage() {
  const sources = useSources()
  const [editing, setEditing] = useState<Editing>(null)
  const close = useCallback(() => setEditing(null), [])

  return (
    <div className="space-y-6">
      <PageHeader
        title="원본 저장소"
        description="배포할 콘텐츠를 읽어 올 저장소입니다. 연결 테스트로 접속과 컬렉션을 확인합니다."
        action={
          <Button onClick={() => setEditing({ mode: 'create' })}>
            <Plus className="size-4" />원본 추가
          </Button>
        }
      />

      <Card>
        {sources.isPending ? (
          <div className="space-y-3 p-5">
            <Skeleton className="h-12" />
            <Skeleton className="h-12" />
          </div>
        ) : sources.isError ? (
          <ErrorState error={sources.error} onRetry={() => void sources.refetch()} />
        ) : sources.data.length === 0 ? (
          <EmptyState
            title="등록된 원본 저장소가 없습니다"
            description="콘텐츠를 읽어 올 저장소를 먼저 등록하세요."
            action={<Button onClick={() => setEditing({ mode: 'create' })}>원본 추가</Button>}
          />
        ) : (
          <Table>
            <thead>
              <tr>
                <Th>이름</Th>
                <Th>콘텐츠 유형</Th>
                <Th>위치</Th>
                <Th>연결</Th>
                <Th>사용</Th>
                <Th className="text-right">관리</Th>
              </tr>
            </thead>
            <tbody>
              {sources.data.map((source) => (
                <SourceRow key={source.sourceConfigId} source={source} onEdit={() => setEditing({ mode: 'edit', source })} />
              ))}
            </tbody>
          </Table>
        )}
      </Card>

      <SidePanel open={editing !== null} title={editing?.mode === 'edit' ? '원본 저장소 수정' : '원본 저장소 추가'} onClose={close}>
        {editing && <SourceForm key={editing.mode === 'edit' ? editing.source.sourceConfigId : 'new'} editing={editing} onDone={close} />}
      </SidePanel>
    </div>
  )
}

function SourceRow({ source, onEdit }: { source: SourceConfig; onEdit: () => void }) {
  const test = useTestSource()
  const remove = useDeleteSource()
  const save = useSaveSource()

  const onDelete = () => {
    if (!window.confirm(`'${source.name}' 원본 저장소를 삭제할까요?\n배포 이력이 있으면 지우지 않고 비활성으로만 돌립니다.`)) return
    remove.mutate(source.sourceConfigId, {
      onSuccess: (result) => result.softDeleted && window.alert('배포 이력이 있어 목록에서만 숨기고 비활성으로 돌렸습니다.'),
      onError: (error) => window.alert(error.message),
    })
  }

  const check = test.data ?? source.lastCheck
  return (
    <tr className={source.enabled ? '' : 'bg-slate-50/60 text-slate-500'}>
      <Td>
        <p className="font-medium text-slate-900">{source.name}</p>
        <p className="text-xs text-slate-400">#{source.sourceConfigId}</p>
      </Td>
      <Td>{SOURCE_TYPE_LABELS[source.sourceType] ?? source.sourceType}</Td>
      <Td>
        <p className="flex items-center gap-1.5">
          <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[11px] font-semibold text-slate-600">{source.dbType}</span>
          <Mono>
            {source.host}:{source.port}
          </Mono>
        </p>
        <Mono className="text-slate-500">
          {source.database}/{source.tableOrCollection}
        </Mono>
      </Td>
      <Td>
        <div className="flex items-center gap-3">
          <ConnectionStatus check={check} />
          {check?.ok && check.documentCount !== null && <span className="text-xs text-slate-500">문서 {formatNumber(check.documentCount)}개</span>}
        </div>
        {check && !check.ok && <p className="mt-1 max-w-56 text-xs text-rose-600">{check.message}</p>}
      </Td>
      <Td>
        <Toggle
          checked={source.enabled}
          label={`${source.name} 사용`}
          disabled={save.isPending}
          onChange={(enabled) => save.mutate({ id: source.sourceConfigId, input: { ...toInput(source), enabled } })}
        />
      </Td>
      <Td className="text-right">
        <div className="flex justify-end gap-1">
          <Button variant="ghost" className="px-2" onClick={() => test.mutate(source.sourceConfigId)} disabled={test.isPending} title="연결 테스트">
            {test.isPending ? <Loader2 className="size-4 animate-spin" /> : <Plug className="size-4" />}
          </Button>
          <Button variant="ghost" className="px-2" onClick={onEdit} title="수정">
            <Pencil className="size-4" />
          </Button>
          <Button variant="ghost" className="px-2 text-rose-600 hover:bg-rose-50 hover:text-rose-700" onClick={onDelete} disabled={remove.isPending} title="삭제">
            <Trash2 className="size-4" />
          </Button>
        </div>
      </Td>
    </tr>
  )
}

function toInput(source: SourceConfig): SourceConfigInput {
  const { sourceConfigId: _id, lastCheck: _check, ...rest } = source
  return { ...rest, password: '' }
}

const EMPTY: SourceConfigInput = {
  name: '',
  sourceType: 'CURRICULUM',
  dbType: 'MONGO',
  host: '',
  port: 27017,
  database: '',
  username: '',
  password: '',
  tableOrCollection: '',
  enabled: true,
}

function SourceForm({ editing, onDone }: { editing: NonNullable<Editing>; onDone: () => void }) {
  // 추가한 뒤 연결 테스트가 실패해 다시 저장하면 새로 만들지 않고 고친다
  const [savedId, setSavedId] = useState<number | null>(editing.mode === 'edit' ? editing.source.sourceConfigId : null)
  const creating = savedId === null
  const [form, setForm] = useState<SourceConfigInput>(editing.mode === 'create' ? EMPTY : toInput(editing.source))
  const [touched, setTouched] = useState(false)
  const [testResult, setTestResult] = useState<ConnectionCheck | null>(null)
  const save = useSaveSource()
  const test = useTestSource()

  const set = <K extends keyof SourceConfigInput>(key: K, value: SourceConfigInput[K]) => setForm((f) => ({ ...f, [key]: value }))

  const errors = {
    name: !form.name.trim() ? '이름을 입력하세요.' : null,
    host: !form.host.trim() ? '호스트를 입력하세요.' : null,
    port: !Number.isInteger(form.port) || form.port <= 0 || form.port > 65535 ? '1~65535 사이로 입력하세요.' : null,
    database: !form.database.trim() ? 'DB 이름을 입력하세요.' : null,
    tableOrCollection: !form.tableOrCollection.trim() ? '컬렉션(테이블)을 입력하세요.' : null,
    username: !form.username.trim() ? '사용자를 입력하세요.' : null,
    password: creating && !form.password ? '비밀번호를 입력하세요.' : null,
  }
  const valid = Object.values(errors).every((e) => e === null)
  const shown = (key: keyof typeof errors) => (touched ? errors[key] : null)

  const submit = (e: FormEvent) => {
    e.preventDefault()
    setTouched(true)
    if (!valid) return
    save.mutate(
      { id: savedId, input: form },
      {
        // 저장하고 바로 연결 테스트를 돌려 결과를 보여 준다
        onSuccess: (saved) => {
          setSavedId(saved.sourceConfigId)
          test.mutate(saved.sourceConfigId, {
            onSuccess: (result) => {
              setTestResult(result)
              if (result.ok) onDone()
            },
          })
        },
      },
    )
  }

  return (
    <form onSubmit={submit} className="space-y-4" noValidate>
      <Field label="이름" error={shown('name')} hint="화면에 보일 이름. 예: 저작DB (운영)">
        <Input value={form.name} onChange={(e) => set('name', e.target.value)} autoFocus />
      </Field>
      <div className="grid grid-cols-2 gap-3">
        <Field label="콘텐츠 유형">
          <Select value={form.sourceType} onChange={(e) => set('sourceType', e.target.value as SourceType)}>
            <option value="CURRICULUM">커리큘럼</option>
            <option value="EXAM_RESULT_DATA" disabled>
              시험 결과 (준비 중)
            </option>
          </Select>
        </Field>
        <Field label="DB 종류">
          <Select
            value={form.dbType}
            onChange={(e) => {
              const dbType = e.target.value as DbType
              setForm((f) => ({ ...f, dbType, port: f.port === DEFAULT_PORT[f.dbType] ? DEFAULT_PORT[dbType] : f.port }))
            }}
          >
            <option value="MONGO">MongoDB</option>
            <option value="MYSQL">MySQL</option>
          </Select>
        </Field>
      </div>
      <div className="grid grid-cols-[1fr_7rem] gap-3">
        <Field label="호스트" error={shown('host')}>
          <Input value={form.host} onChange={(e) => set('host', e.target.value)} placeholder="mongo.authoring.svc" />
        </Field>
        <Field label="포트" error={shown('port')}>
          <Input type="number" value={form.port} onChange={(e) => set('port', Number(e.target.value))} />
        </Field>
      </div>
      <div className="grid grid-cols-2 gap-3">
        <Field label="DB" error={shown('database')}>
          <Input value={form.database} onChange={(e) => set('database', e.target.value)} placeholder="learningDB" />
        </Field>
        <Field label={form.dbType === 'MONGO' ? '컬렉션' : '테이블'} error={shown('tableOrCollection')}>
          <Input value={form.tableOrCollection} onChange={(e) => set('tableOrCollection', e.target.value)} placeholder="CurriculumData" />
        </Field>
      </div>
      <div className="grid grid-cols-2 gap-3">
        <Field label="사용자" error={shown('username')}>
          <Input value={form.username} onChange={(e) => set('username', e.target.value)} autoComplete="off" />
        </Field>
        <Field label="비밀번호" error={shown('password')} hint={creating ? undefined : '비워 두면 기존 값을 유지합니다.'}>
          <Input type="password" value={form.password} onChange={(e) => set('password', e.target.value)} autoComplete="new-password" placeholder={creating ? '' : '••••••••'} />
        </Field>
      </div>
      <label className="flex items-center justify-between rounded-lg border border-slate-200 px-3 py-2.5">
        <span className="text-sm">
          <span className="font-medium text-slate-700">사용</span>
          <span className="ml-2 text-slate-500">끄면 이 원본으로 배포할 수 없습니다.</span>
        </span>
        <Toggle checked={form.enabled} onChange={(v) => set('enabled', v)} label="사용" />
      </label>

      {save.isError && <Notice tone="danger" title="저장하지 못했습니다">{save.error.message}</Notice>}
      {testResult && !testResult.ok && (
        <Notice tone="warning" title="저장했지만 연결 테스트에 실패했습니다">
          {testResult.message}
          <br />
          접속 정보를 확인하고 다시 저장하거나, 그대로 닫아도 됩니다.
        </Notice>
      )}

      <div className="flex justify-end gap-2 pt-2">
        <Button variant="secondary" onClick={onDone}>
          {testResult && !testResult.ok ? '닫기' : '취소'}
        </Button>
        <Button type="submit" disabled={save.isPending || test.isPending}>
          {save.isPending || test.isPending ? <Loader2 className="size-4 animate-spin" /> : <Database className="size-4" />}
          {save.isPending ? '저장 중' : test.isPending ? '연결 확인 중' : '저장하고 연결 테스트'}
        </Button>
      </div>
    </form>
  )
}
