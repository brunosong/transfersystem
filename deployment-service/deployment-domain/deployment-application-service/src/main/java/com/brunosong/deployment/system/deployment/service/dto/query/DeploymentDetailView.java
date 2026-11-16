package com.brunosong.deployment.system.deployment.service.dto.query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 배포 상세. 화면(배포 상세)이 바로 쓰는 모양이다(console-plan 4장).
 * 전에는 사가 모양(단계 목록 steps)으로 내려 주었는데, 사가를 미뤄서 대상 하나와 그 청크들을 바로 담는다.
 * sourceTitle 은 배포가 커리큘럼 제목을 기억하지 않아 아직 null 이다.
 */
public record DeploymentDetailView(
        UUID deploymentId,
        String sourceId,
        String sourceType,
        Long sourceConfigId,
        String sourceTitle,
        Long targetId,
        String status,
        String resultMessage,
        DeploymentProgressView progress,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ChunkView> chunks) {

    /**
     * 청크 하나. label 은 운영자가 알아볼 이름이다. 과목 이름은 청크 표에 없어서 번호로 보여 준다.
     * retryCount 는 재전송 횟수다. 첫 시도가 1이므로 시도 번호에서 1을 뺀다. 아직 안 보낸 청크는 시도 번호가 0이라 0으로 둔다.
     */
    public record ChunkView(int chunkOffset, String label, String status, Instant processedAt,
                            String errorMessage, int retryCount) {

        public static ChunkView from(ChunkRecord chunk) {
            String label = chunk.chunkOffset() == 0 ? "뼈대" : "과목 " + chunk.chunkOffset();
            return new ChunkView(chunk.chunkOffset(), label, chunk.status(), chunk.processedAt(),
                    chunk.errorMessage(), Math.max(0, chunk.attempt() - 1));
        }
    }
}
