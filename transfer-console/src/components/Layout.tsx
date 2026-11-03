import clsx from 'clsx'
import { BookOpen, Database, FlaskConical, History, LayoutDashboard, Library, Plus, RotateCcw, Server, UserRound } from 'lucide-react'
import { Link, NavLink, Outlet } from 'react-router-dom'
import { api } from '../api'
import { resetMockData } from '../api/mock/store'
import { useHealth } from '../hooks/queries'
import { OPERATORS, setCurrentUser, useCurrentUser } from '../lib/user'
import { Button } from './ui'

type Icon = typeof Plus

const NAV: { title: string; items: { to: string; label: string; icon: Icon; end?: boolean }[] }[] = [
  {
    title: '배포',
    items: [
      { to: '/', label: '대시보드', icon: LayoutDashboard, end: true },
      { to: '/contents', label: '콘텐츠', icon: Library },
      { to: '/deployments', label: '배포 이력', icon: History },
    ],
  },
  {
    title: '설정',
    items: [
      { to: '/settings/sources', label: '원본 저장소', icon: Database },
      { to: '/settings/targets', label: '대상 시스템', icon: Server },
    ],
  },
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
      <Link to="/" className="flex h-16 items-center gap-2.5 px-5">
        <div className="grid size-8 place-items-center rounded-lg bg-brand-600 text-white shadow-sm shadow-brand-600/30">
          <Library className="size-4" strokeWidth={2.5} />
        </div>
        <div className="leading-tight">
          <p className="text-[15px] font-semibold tracking-tight">콘텐츠 배포 콘솔</p>
          <p className="text-[11px] text-slate-500">저작 콘텐츠를 서비스로</p>
        </div>
      </Link>

      <nav className="mt-4 flex flex-col gap-5 px-3">
        {NAV.map((group) => (
          <div key={group.title} className="flex flex-col gap-0.5">
            <p className="px-2 pb-2 text-[11px] font-medium tracking-wide text-slate-400">{group.title}</p>
            {group.items.map((item) => (
              <SideLink key={item.to} {...item} />
            ))}
          </div>
        ))}
      </nav>

      <div className="mt-auto space-y-3 p-4">
        {api.mode === 'mock' && <MockPanel />}
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

function MockPanel() {
  return (
    <div className="rounded-lg border border-amber-200 bg-amber-50 p-3 text-xs text-amber-900">
      <p className="flex items-center gap-1.5 font-semibold">
        <FlaskConical className="size-3.5" /> 모의 데이터 모드
      </p>
      <ul className="mt-1.5 list-disc space-y-0.5 pl-4 leading-relaxed text-amber-800">
        <li>
          <code className="font-mono">CUR-FAIL-01</code> 을 배포하면 청크 두 개가 실패합니다.
        </li>
        <li>
          호스트에 <code className="font-mono">down</code> 이 들어간 원본은 연결에 실패합니다.
        </li>
      </ul>
      <button
        type="button"
        onClick={() => {
          if (!window.confirm('직접 만든 원본과 배포 이력을 지우고 처음 상태로 돌립니다.')) return
          resetMockData()
          window.location.reload()
        }}
        className="mt-2 inline-flex items-center gap-1 font-medium text-amber-900 underline-offset-2 hover:underline"
      >
        <RotateCcw className="size-3" /> 모의 데이터 초기화
      </button>
    </div>
  )
}

function SideLink({ to, label, icon: Icon, end }: { to: string; label: string; icon: Icon; end?: boolean }) {
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
    <header className="sticky top-0 z-20 flex h-16 items-center justify-between gap-4 border-b border-slate-200 bg-white/80 px-4 backdrop-blur sm:px-8">
      <nav className="flex items-center gap-1 overflow-x-auto lg:hidden">
        {NAV.flatMap((g) => g.items).map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              clsx('rounded-md px-2.5 py-1.5 text-sm font-medium whitespace-nowrap', isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600')
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
      <div className="hidden lg:block" />
      <div className="flex items-center gap-3">
        <ConnectionIndicator />
        <UserSelect />
        <Link to="/deployments/new" className="hidden sm:block">
          <Button>
            <Plus className="size-4" />새 배포
          </Button>
        </Link>
      </div>
    </header>
  )
}

function UserSelect() {
  const user = useCurrentUser()
  return (
    <label className="inline-flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white py-1 pr-1 pl-2.5 text-sm text-slate-700" title="배포 요청자">
      <UserRound className="size-4 text-slate-400" />
      <select value={user} onChange={(e) => setCurrentUser(e.target.value)} className="bg-transparent py-0.5 pr-1 focus:outline-none" aria-label="요청자">
        {OPERATORS.map((name) => (
          <option key={name}>{name}</option>
        ))}
      </select>
    </label>
  )
}

function ConnectionIndicator() {
  const { data: healthy, isPending } = useHealth()
  if (api.mode === 'mock') return <Pill dot="bg-amber-500" text="모의 데이터" />
  if (isPending) return <Pill dot="bg-slate-300" text="연결 확인 중" />
  return healthy ? <Pill dot="bg-emerald-500" text="서버 연결됨" /> : <Pill dot="bg-rose-500" text="서버 연결 안 됨" />
}

function Pill({ dot, text }: { dot: string; text: string }) {
  return (
    <span className="hidden items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-1 text-xs font-medium text-slate-600 md:inline-flex">
      <span className={clsx('size-1.5 rounded-full', dot)} />
      {text}
    </span>
  )
}
