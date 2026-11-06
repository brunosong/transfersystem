package com.brunosong.transfer.system.deployment.service.dto.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 배포 상세. 사가 모양(단계 목록)으로 내려 준다.
 */
public record DeploymentDetailView(
        UUID deploymentId,
        String sourceId,
        String sourceType,
        Long sourceConfigId,
        String status,
        String resultMessage,
        DeploymentProgressView progress,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<DeploymentStepView> steps) {
}
