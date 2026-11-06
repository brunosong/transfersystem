package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChunkTest {

    @Test
    @DisplayName("처음 보낸 청크는 SENT 상태이고 시도 번호가 1이다.")
    void newSentChunkHasFirstAttempt() {
        Chunk chunk = Chunk.sent(0);

        assertEquals(0, chunk.getNo());
        assertEquals(ChunkStatus.SENT, chunk.getStatus());
        assertEquals(1, chunk.getAttempt());
    }

    @Test
    @DisplayName("보낸 청크에 성공 결과가 오면 APPLIED 가 된다")
    void sentChunkBecomesAppliedOnSuccess() {
        Chunk chunk = Chunk.sent(0);

        chunk.recordSuccess();

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }
}
