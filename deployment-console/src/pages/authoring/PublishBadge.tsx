import clsx from 'clsx'

/**
 * 공개 상태. 공개 기능이 아직 없어서 지금은 늘 "공개 전"이다.
 * 공개가 생기면 판 번호(version)와 "공개 뒤 고침" 여부를 받아 보여 준다
 */
export function PublishBadge({ version, changedSincePublish = false }: { version?: number; changedSincePublish?: boolean }) {
  const [label, tone] =
    version === undefined
      ? ['공개 전', 'bg-slate-100 text-slate-600 ring-slate-200']
      : changedSincePublish
        ? [`판 ${version} · 고치는 중`, 'bg-amber-50 text-amber-800 ring-amber-200']
        : [`판 ${version} 공개됨`, 'bg-emerald-50 text-emerald-700 ring-emerald-200']
  return <span className={clsx('rounded-full px-2 py-0.5 text-xs font-medium ring-1 ring-inset', tone)}>{label}</span>
}
