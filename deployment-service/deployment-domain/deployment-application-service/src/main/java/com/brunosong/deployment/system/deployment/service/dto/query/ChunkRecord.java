package com.brunosong.deployment.system.deployment.service.dto.query;

import java.time.Instant;

/**
 * 청크 한 개의 지금 상태. 새 청크 표(deployment_chunk)에서 읽는다.
 * status 는 WAITING, SENT, APPLIED, FAILED 중 하나다.
 * processedAt 은 실패한 청크면 실패한 시각, 그 밖에는 보낸 시각이다. 아직 안 보냈으면 null 이다.
 * 반영된 시각은 따로 기록하지 않는다.
 */
public record ChunkRecord(int chunkOffset, String status, Instant processedAt,
                          String errorMessage, int attempt) {
}