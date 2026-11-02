package com.brunosong.transfer.system.transfer.service.dto.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 전송 상세. 사가 모양(단계 목록)으로 내려 준다.
 */
public record TransferDetailView(
        UUID transferId,
        String sourceId,
        String sourceType,
        Long sourceConfigId,
        String status,
        String resultMessage,
        TransferProgressView progress,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TransferStepView> steps) {
}
