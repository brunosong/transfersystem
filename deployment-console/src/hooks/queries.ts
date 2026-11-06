import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api, isNotFound } from '../api'
import type { CreateDeploymentRequest, CurriculumQuery, SourceConfigInput, StatsQuery, DeploymentQuery } from '../api'
import { isActiveDeployment } from '../lib/status'
import { useCurrentUser } from '../lib/user'

/** 진행 중인 배포가 있을 때만 짧게 다시 읽는다. 끝난 화면은 조용히 둔다. */
const LIVE_INTERVAL = 1500
const IDLE_INTERVAL = 15_000

export const keys = {
  health: ['health'] as const,
  sources: ['sources'] as const,
  curriculums: (sourceId: number, query: CurriculumQuery) => ['curriculums', sourceId, query] as const,
  curriculum: (sourceId: number, curriculumId: string) => ['curriculum', sourceId, curriculumId] as const,
  curriculumDeployments: (curriculumId: string) => ['curriculum-deployments', curriculumId] as const,
  targets: ['targets'] as const,
  targetCurriculums: (targetId: number) => ['target-curriculums', targetId] as const,
  targetCurriculum: (targetId: number, curriculumId: string) => ['target-curriculum', targetId, curriculumId] as const,
  deployments: (query: DeploymentQuery) => ['deployments', query] as const,
  deployment: (id: string) => ['deployment', id] as const,
  stats: (query: StatsQuery) => ['deployment-stats', query] as const,
}

const noRetryOn404 = (count: number, error: Error) => !isNotFound(error) && count < 2

export function useHealth() {
  return useQuery({ queryKey: keys.health, queryFn: () => api.health(), refetchInterval: 10_000, retry: false })
}

// ─── 원본 저장소 ──────────────────────────────────────────────

export function useSources() {
  return useQuery({ queryKey: keys.sources, queryFn: () => api.listSourceConfigs(), staleTime: 30_000 })
}

export function useSaveSource() {
  const client = useQueryClient()
  return useMutation({
    mutationFn: ({ id, input }: { id: number | null; input: SourceConfigInput }) =>
      id === null ? api.createSourceConfig(input) : api.updateSourceConfig(id, input),
    onSuccess: () => void client.invalidateQueries({ queryKey: keys.sources }),
  })
}

export function useDeleteSource() {
  const client = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => api.deleteSourceConfig(id),
    onSuccess: () => void client.invalidateQueries({ queryKey: keys.sources }),
  })
}

export function useTestSource() {
  const client = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => api.testSourceConfig(id),
    onSuccess: () => void client.invalidateQueries({ queryKey: keys.sources }),
  })
}

// ─── 콘텐츠 ──────────────────────────────────────────────────

export function useCurriculums(sourceId: number | null, query: CurriculumQuery) {
  return useQuery({
    queryKey: keys.curriculums(sourceId ?? -1, query),
    queryFn: () => api.listCurriculums(sourceId!, query),
    enabled: sourceId !== null,
    placeholderData: keepPreviousData,
    retry: false,
  })
}

export function useCurriculum(sourceId: number | null, curriculumId: string | null) {
  return useQuery({
    queryKey: keys.curriculum(sourceId ?? -1, curriculumId ?? ''),
    queryFn: () => api.getCurriculum(sourceId!, curriculumId!),
    enabled: sourceId !== null && !!curriculumId,
    staleTime: 30_000,
    retry: noRetryOn404,
  })
}

export function useCurriculumDeployments(curriculumId: string | null) {
  return useQuery({
    queryKey: keys.curriculumDeployments(curriculumId ?? ''),
    queryFn: () => api.listCurriculumDeployments(curriculumId!),
    enabled: !!curriculumId,
    refetchInterval: (q) => (q.state.data?.some((d) => d.last && isActiveDeployment(d.last.status)) ? LIVE_INTERVAL : false),
  })
}

// ─── 대상 시스템 ─────────────────────────────────────────────

export function useTargets() {
  return useQuery({ queryKey: keys.targets, queryFn: () => api.listTargets(), staleTime: 30_000 })
}

export function useTestTarget() {
  const client = useQueryClient()
  return useMutation({
    mutationFn: (targetId: number) => api.testTarget(targetId),
    onSuccess: () => void client.invalidateQueries({ queryKey: keys.targets }),
  })
}

export function useSetTargetEnabled() {
  const client = useQueryClient()
  return useMutation({
    mutationFn: ({ targetId, enabled }: { targetId: number; enabled: boolean }) => api.setTargetEnabled(targetId, enabled),
    onSuccess: () => void client.invalidateQueries({ queryKey: keys.targets }),
  })
}

export function useTargetCurriculums(targetId: number | null) {
  return useQuery({
    queryKey: keys.targetCurriculums(targetId ?? -1),
    queryFn: () => api.listTargetCurriculums(targetId!),
    enabled: targetId !== null,
  })
}

export function useTargetCurriculum(targetId: number, curriculumId: string) {
  return useQuery({
    queryKey: keys.targetCurriculum(targetId, curriculumId),
    queryFn: () => api.getTargetCurriculum(targetId, curriculumId),
    retry: noRetryOn404,
  })
}

// ─── 배포 ────────────────────────────────────────────────────

export function useDeployments(query: DeploymentQuery, options: { enabled?: boolean } = {}) {
  return useQuery({
    queryKey: keys.deployments(query),
    queryFn: () => api.listDeployments(query),
    placeholderData: keepPreviousData,
    enabled: options.enabled ?? true,
    refetchInterval: (q) => (q.state.data?.items.some((t) => isActiveDeployment(t.status)) ? LIVE_INTERVAL : IDLE_INTERVAL),
  })
}

export function useDeployment(deploymentId: string) {
  return useQuery({
    queryKey: keys.deployment(deploymentId),
    queryFn: () => api.getDeployment(deploymentId),
    refetchInterval: (q) => (q.state.data && isActiveDeployment(q.state.data.status) ? LIVE_INTERVAL : false),
    retry: noRetryOn404,
  })
}

export function useStatusCounts(query: StatsQuery = {}) {
  return useQuery({ queryKey: keys.stats(query), queryFn: () => api.countByStatus(query), refetchInterval: 5000 })
}

/** 배포가 바뀌면 함께 바뀌는 조회를 모두 다시 읽게 한다 */
function invalidateDeployments(client: ReturnType<typeof useQueryClient>) {
  for (const key of ['deployments', 'deployment', 'deployment-stats', 'curriculums', 'curriculum-deployments', 'target-curriculums', 'target-curriculum']) {
    void client.invalidateQueries({ queryKey: [key] })
  }
}

export function useCreateDeployment() {
  const client = useQueryClient()
  const user = useCurrentUser()
  return useMutation({
    mutationFn: (request: CreateDeploymentRequest) => api.createDeployment(request, user),
    onSuccess: () => invalidateDeployments(client),
  })
}

export function useRetryDeployment() {
  const client = useQueryClient()
  const user = useCurrentUser()
  return useMutation({
    mutationFn: (deploymentId: string) => api.retryDeployment(deploymentId, user),
    onSuccess: () => invalidateDeployments(client),
  })
}
