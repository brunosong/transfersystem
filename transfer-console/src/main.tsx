import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, Link, Navigate, RouterProvider, useParams } from 'react-router-dom'
import { Layout } from './components/Layout'
import { Button, Card, EmptyState } from './components/ui'
import './index.css'
import { ContentsPage } from './pages/ContentsPage'
import { DashboardPage } from './pages/DashboardPage'
import { DeploymentDetailPage } from './pages/DeploymentDetailPage'
import { DeploymentsPage } from './pages/DeploymentsPage'
import { NewDeploymentPage } from './pages/NewDeploymentPage'
import { SourcesPage } from './pages/SourcesPage'
import { TargetCurriculumPage } from './pages/TargetCurriculumPage'
import { TargetsPage } from './pages/TargetsPage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { refetchOnWindowFocus: true, staleTime: 1000 },
  },
})

/** 예전 주소(/transfers/...)로 들어와도 새 화면으로 보낸다 */
function LegacyTransferRedirect() {
  const { transferId } = useParams()
  return <Navigate to={transferId ? `/deployments/${transferId}` : '/deployments'} replace />
}

const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'contents', element: <ContentsPage /> },
      { path: 'deployments', element: <DeploymentsPage /> },
      { path: 'deployments/new', element: <NewDeploymentPage /> },
      { path: 'deployments/:transferId', element: <DeploymentDetailPage /> },
      { path: 'targets/:targetId/curriculums/:curriculumId', element: <TargetCurriculumPage /> },
      { path: 'settings', element: <Navigate to="/settings/sources" replace /> },
      { path: 'settings/sources', element: <SourcesPage /> },
      { path: 'settings/targets', element: <TargetsPage /> },
      { path: 'transfers/new', element: <Navigate to="/deployments/new" replace /> },
      { path: 'transfers/:transferId?', element: <LegacyTransferRedirect /> },
      {
        path: '*',
        element: (
          <Card>
            <EmptyState
              title="페이지를 찾을 수 없습니다"
              action={
                <Link to="/">
                  <Button variant="secondary">대시보드로</Button>
                </Link>
              }
            />
          </Card>
        ),
      },
    ],
  },
])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  </StrictMode>,
)
