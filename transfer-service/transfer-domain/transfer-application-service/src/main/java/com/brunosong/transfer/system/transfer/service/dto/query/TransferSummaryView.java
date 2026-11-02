package com.brunosong.transfer.system.transfer.service.dto.query;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 전송 목록의 한 줄.
 */
public record TransferSummaryView(
        UUID transferId,
        String sourceId,
        String sourceType,
        String status,
        TransferProgressView progress,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
