package com.brunosong.deployment.system.deployment.service.dto.query;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 출력 포트가 돌려주는 배포 한 건의 읽기 모델. 청크 집계까지 담는다.
 * 화면용 모양(단계, 진행률)은 애플리케이션 서비스가 여기서 만든다.
 */
public record DeploymentRecord(
        UUID deploymentId,
        String sourceId,
        String sourceType,
        Long sourceConfigId,
        Long targetId,
        DeploymentStatus status,
        int totalChunks,
        long succeededChunks,
        long failedChunks,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String resultMessage) {
}
