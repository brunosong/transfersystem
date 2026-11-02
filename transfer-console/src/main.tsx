import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, Link, Navigate, RouterProvider } from 'react-router-dom'
import { Layout } from './components/Layout'
import { Button, Card, EmptyState } from './components/ui'
import './index.css'
import { NewTransferPage } from './pages/NewTransferPage'
import { TransferDetailPage } from './pages/TransferDetailPage'
import { TransferListPage } from './pages/TransferListPage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { refetchOnWindowFocus: true, staleTime: 1000 },
  },
})

const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/transfers" replace /> },
      { path: 'transfers', element: <TransferListPage /> },
      { path: 'transfers/new', element: <NewTransferPage /> },
      { path: 'transfers/:transferId', element: <TransferDetailPage /> },
      {
        path: '*',
        element: (
          <Card>
            <EmptyState
              title="페이지를 찾을 수 없습니다"
              action={
                <Link to="/transfers">
                  <Button variant="secondary">전송 현황으로</Button>
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
