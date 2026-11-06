package com.brunosong.deployment.system.deployment.service.dto.query;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배포 목록의 한 줄.
 */
public record DeploymentSummaryView(
        UUID deploymentId,
        String sourceId,
        String sourceType,
        String status,
        DeploymentProgressView progress,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
