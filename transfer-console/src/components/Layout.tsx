import clsx from 'clsx'
import { ArrowLeftRight, BookOpen, FlaskConical, LayoutList, Plus } from 'lucide-react'
import type { ReactNode } from 'react'
import { NavLink, Outlet } from 'react-router-dom'
import { api } from '../api'
import { useHealth } from '../hooks/queries'

const NAV = [
  { to: '/transfers', label: '전송 현황', icon: LayoutList, end: true },
  { to: '/transfers/new', label: '새 전송', icon: Plus, end: true },
]

export function Layout() {
  return (
    <div className="min-h-screen lg:pl-60">
      <Sidebar />
      <div className="flex min-h-screen flex-col">
        <TopBar />
        <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8 sm:px-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

function Sidebar() {
  return (
    <aside className="fixed inset-y-0 left-0 hidden w-60 flex-col border-r border-slate-200 bg-white lg:flex">
      <div className="flex h-16 items-center gap-2.5 px-5">
        <div className="grid size-8 place-items-center rounded-lg bg-brand-600 text-white shadow-sm shadow-brand-600/30">
          <ArrowLeftRight className="size-4" strokeWidth={2.5} />
        </div>
        <div className="leading-tight">
          <p className="text-[15px] font-semibold tracking-tight">Transfer Console</p>
          <p className="text-[11px] text-slate-500">커리큘럼 이관 관리</p>
        </div>
      </div>

      <nav className="mt-4 flex flex-col gap-0.5 px-3">
        <p className="px-2 pb-2 text-[11px] font-medium tracking-wide text-slate-400 uppercase">이관</p>
        {NAV.map((item) => (
          <SideLink key={item.to} {...item} />
        ))}
      </nav>

      <div className="mt-auto space-y-3 p-4">
        {api.mode === 'mock' && (
          <div className="rounded-lg border border-amber-200 bg-amber-50 p-3 text-xs text-amber-900">
            <p className="flex items-center gap-1.5 font-semibold">
              <FlaskConical className="size-3.5" /> 모의 데이터 모드
            </p>
            <p className="mt-1 leading-relaxed text-amber-800">
              백엔드 없이 브라우저 안에서 사가 흐름을 흉내 냅니다. 원본 ID에 <code className="font-mono">fail</code>이
              들어가면 보상 흐름을 볼 수 있습니다.
            </p>
          </div>
        )}
        <a
          href="https://github.com/brunosong/transfersystem"
          target="_blank"
          rel="noreferrer"
          className="flex items-center gap-2 rounded-lg px-2 py-1.5 text-xs text-slate-500 transition hover:bg-slate-100 hover:text-slate-800"
        >
          <BookOpen className="size-3.5" /> 저장소와 설계 문서
        </a>
      </div>
    </aside>
  )
}

function SideLink({ to, label, icon: Icon, end }: { to: string; label: string; icon: typeof Plus; end: boolean }) {
  return (
    <NavLink
      to={to}
      end={end}
      className={({ isActive }) =>
        clsx(
          'flex items-center gap-2.5 rounded-lg px-2.5 py-2 text-sm font-medium transition',
          isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
        )
      }
    >
      <Icon className="size-4" />
      {label}
    </NavLink>
  )
}

function TopBar() {
  return (
    <header className="sticky top-0 z-10 flex h-16 items-center justify-between gap-4 border-b border-slate-200 bg-white/80 px-4 backdrop-blur sm:px-8">
      <nav className="flex items-center gap-1 lg:hidden">
        {NAV.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              clsx('rounded-md px-2.5 py-1.5 text-sm font-medium', isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600')
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
      <div className="hidden text-sm text-slate-500 lg:block">원본 저장소의 커리큘럼을 대상 시스템으로 이관합니다</div>
      <ConnectionIndicator />
    </header>
  )
}

function ConnectionIndicator() {
  const { data: healthy, isPending } = useHealth()
  let content: ReactNode
  if (api.mode === 'mock') {
    content = <Pill dot="bg-amber-500" text="모의 데이터" />
  } else if (isPending) {
    content = <Pill dot="bg-slate-300" text="연결 확인 중" />
  } else if (healthy) {
    content = <Pill dot="bg-emerald-500" text="transfer-service 연결됨" />
  } else {
    content = <Pill dot="bg-rose-500" text="transfer-service 연결 안 됨" />
  }
  return content
}

function Pill({ dot, text }: { dot: string; text: string }) {
  return (
    <span className="inline-flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-1 text-xs font-medium text-slate-600">
      <span className={clsx('size-1.5 rounded-full', dot)} />
      {text}
    </span>
  )
}
