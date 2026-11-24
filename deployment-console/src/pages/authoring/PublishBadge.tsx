import clsx from 'clsx'

/**
 * 공개 상태.
 * - 공개 전: 판이 없다. 배포에 보이지 않는다
 * - 판 N 공개됨: 초안이 가장 최근 판과 같다
 * - 판 N · 고치는 중: 공개한 뒤 초안을 고쳤다. 배포는 아직 판 N 을 읽는다
 */
export function PublishBadge({ version, changedSincePublish = false }: { version?: number | null; changedSincePublish?: boolean }) {
  const [label, tone] =
    version === undefined || version === null
      ? ['공개 전', 'bg-slate-100 text-slate-600 ring-slate-200']
      : changedSincePublish
        ? [`판 ${version} · 고치는 중`, 'bg-amber-50 text-amber-800 ring-amber-200']
        : [`판 ${version} 공개됨`, 'bg-emerald-50 text-emerald-700 ring-emerald-200']
  return <span className={clsx('rounded-full px-2 py-0.5 text-xs font-medium whitespace-nowrap ring-1 ring-inset', tone)}>{label}</span>
}
