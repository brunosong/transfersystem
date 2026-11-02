package com.brunosong.transfer.system.transfer.service.dto.query;

import java.time.Instant;

/**
 * 대상 서비스가 처리 결과를 돌려준 청크 한 개. status 는 SUCCESS, FAILED 처럼 대문자로 맞춰 둔다.
 */
public record ChunkRecord(int chunkOffset, String status, Instant processedAt) {
}
