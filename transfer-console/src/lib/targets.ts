// 대상 시스템. data-migration-service 의 datamigrations 시드와 같은 ID 를 쓴다.
// transfer-service 는 아직 대상 이름을 모르므로(단계에 targetId 만 옴) 화면이 이 표로 이름을 붙인다.

export type TargetSystem = 'BRUNOSONG_ONLINE_CAMPUS' | 'BRUNOSONG_BOOTCAMP'
export type TargetEnvironment = 'PROD' | 'DEV'

export interface Target {
  id: number
  system: TargetSystem
  environment: TargetEnvironment
}

export const TARGETS: Target[] = [
  { id: 1, system: 'BRUNOSONG_ONLINE_CAMPUS', environment: 'PROD' },
  { id: 2, system: 'BRUNOSONG_BOOTCAMP', environment: 'PROD' },
  { id: 3, system: 'BRUNOSONG_ONLINE_CAMPUS', environment: 'DEV' },
  { id: 4, system: 'BRUNOSONG_BOOTCAMP', environment: 'DEV' },
]

const SYSTEM_LABELS: Record<string, string> = {
  BRUNOSONG_ONLINE_CAMPUS: '온라인캠퍼스',
  BRUNOSONG_BOOTCAMP: '부트캠프',
}

export function systemLabel(system: string | null | undefined): string {
  if (!system) return '알 수 없는 대상'
  return SYSTEM_LABELS[system] ?? system
}

export function targetLabel(targetId: number | null, targetSystem: string | null): string {
  if (targetSystem) return systemLabel(targetSystem)
  const target = TARGETS.find((t) => t.id === targetId)
  if (!target) return targetId == null ? '알 수 없는 대상' : `대상 #${targetId}`
  return `${systemLabel(target.system)} · ${target.environment}`
}
