// 대상 시스템 이름 붙이기. 대상 목록은 data-migration-service 에서 받고, 여기서는 화면에 쓸 이름만 정한다.

import type { Target } from '../api/types'

const SYSTEM_LABELS: Record<string, string> = {
  BRUNOSONG_ONLINE_CAMPUS: '온라인캠퍼스',
  BRUNOSONG_BOOTCAMP: '부트캠프',
}

export function systemLabel(system: string | null | undefined): string {
  if (!system) return '알 수 없는 대상'
  return SYSTEM_LABELS[system] ?? system
}

export function targetLabel(target: Pick<Target, 'system' | 'environment'>): string {
  return `${systemLabel(target.system)} · ${target.environment}`
}

/** 목록에 대상 ID 만 있을 때 이름을 찾아 붙인다 */
export function targetLabelById(targets: Target[] | undefined, targetId: number): string {
  const target = targets?.find((t) => t.targetId === targetId)
  return target ? targetLabel(target) : `대상 #${targetId}`
}

/** 화면에서 대상을 늘어놓는 순서: 시스템, 그다음 DEV → PROD */
export function sortTargets(targets: Target[]): Target[] {
  return [...targets].sort((a, b) =>
    a.system === b.system ? a.environment.localeCompare(b.environment) : a.system.localeCompare(b.system) * -1,
  )
}
