package com.brunosong.deployment.system.deployment.service.dto.query;

import java.util.List;

/**
 * 사가의 한 단계. 지금은 대상 하나에 데이터를 반영하는 단계(APPLY) 하나뿐이다.
 * 사가가 들어오면 온라인캠퍼스 APPLY, 부트캠프 APPLY, 그리고 보상 단계(COMPENSATE)가 더해진다.
 *
 * @param action       APPLY 또는 COMPENSATE
 * @param targetId     대상 설정 ID(data-migration-service 의 datamigration_id)
 * @param targetSystem 대상 시스템 이름. 지금은 deployment-service 가 알지 못해 null 이다
 * @param status       PENDING, PROCESSING, SUCCEEDED, FAILED
 */
public record DeploymentStepView(
        int order,
        String action,
        Long targetId,
        String targetSystem,
        String status,
        DeploymentProgressView progress,
        List<ChunkRecord> chunks) {
}
