import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../api'
import type { CreateTransferRequest, TransferQuery } from '../api'
import { isActiveTransfer } from '../lib/status'

/** 진행 중인 전송이 있을 때만 짧게 다시 읽는다. 끝난 화면은 조용히 둔다. */
const LIVE_INTERVAL = 1500
const IDLE_INTERVAL = 15000

export const keys = {
  transfers: (query: TransferQuery) => ['transfers', query] as const,
  transfer: (id: string) => ['transfer', id] as const,
  stats: ['transfer-stats'] as const,
  sourceConfigs: ['source-configs'] as const,
  health: ['health'] as const,
}

export function useTransfers(query: TransferQuery) {
  return useQuery({
    queryKey: keys.transfers(query),
    queryFn: () => api.listTransfers(query),
    placeholderData: keepPreviousData,
    refetchInterval: (q) =>
      q.state.data?.items.some((t) => isActiveTransfer(t.status)) ? LIVE_INTERVAL : IDLE_INTERVAL,
  })
}

export function useTransfer(transferId: string) {
  return useQuery({
    queryKey: keys.transfer(transferId),
    queryFn: () => api.getTransfer(transferId),
    refetchInterval: (q) => {
      const detail = q.state.data
      if (!detail) return false
      const stepsActive = detail.steps.some((s) => s.status === 'PROCESSING' || s.status === 'COMPENSATING')
      return isActiveTransfer(detail.status) || stepsActive ? LIVE_INTERVAL : false
    },
    retry: (count, error) => !(error instanceof Error && 'status' in error && error.status === 404) && count < 2,
  })
}

export function useStatusCounts() {
  return useQuery({ queryKey: keys.stats, queryFn: () => api.countByStatus(), refetchInterval: 5000 })
}

export function useSourceConfigs() {
  return useQuery({ queryKey: keys.sourceConfigs, queryFn: () => api.listSourceConfigs(), staleTime: 60_000 })
}

export function useHealth() {
  return useQuery({ queryKey: keys.health, queryFn: () => api.health(), refetchInterval: 10_000, retry: false })
}

export function useCreateTransfer() {
  const client = useQueryClient()
  return useMutation({
    mutationFn: (request: CreateTransferRequest) => api.createTransfer(request),
    onSuccess: () => {
      void client.invalidateQueries({ queryKey: ['transfers'] })
      void client.invalidateQueries({ queryKey: keys.stats })
    },
  })
}
