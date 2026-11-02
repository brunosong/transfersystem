package com.brunosong.transfer.system.transfer.service.dto.query;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 출력 포트가 돌려주는 전송 한 건의 읽기 모델. 청크 집계까지 담는다.
 * 화면용 모양(단계, 진행률)은 애플리케이션 서비스가 여기서 만든다.
 */
public record TransferRecord(
        UUID transferId,
        String sourceId,
        String sourceType,
        Long sourceConfigId,
        Long dataMigrationInfoId,
        TransferStatus status,
        int totalChunks,
        long succeededChunks,
        long failedChunks,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String resultMessage) {
}
