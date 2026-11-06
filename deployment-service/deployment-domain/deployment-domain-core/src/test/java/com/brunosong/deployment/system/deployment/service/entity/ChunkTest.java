package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
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

    @Test
    @DisplayName("보낸 청크에 현재 시도의 실패 결과가 오면 FAILED가 된다.")
    void sentChunkBecomesFailedOnCurrentAttemptFailure() {
        Chunk chunk = Chunk.sent(0);

        chunk.recordFailure(1);

        assertEquals(ChunkStatus.FAILED, chunk.getStatus());
    }

    @Test
    @DisplayName("재전송하면 SENT 로 돌아가고 시도 번호가 오른다")
    void retryGoesBackToSentWithNextAttempt() {
        Chunk chunk = Chunk.sent(0);
        chunk.recordFailure(1);

        chunk.retry();

        assertEquals(ChunkStatus.SENT, chunk.getStatus());
        assertEquals(2, chunk.getAttempt());
    }

    @Test
    @DisplayName("이전 시도의 실패 결과가 늦게 오면 무시한다")
    void lateFailureOfPreviousAttemptIsIgnored() {
        Chunk chunk = Chunk.sent(0);
        chunk.recordFailure(1);

        chunk.retry();

        chunk.recordFailure(1);

        assertEquals(ChunkStatus.SENT, chunk.getStatus());
    }

    @Test
    @DisplayName("실패한 청크에 성공 결과가 늦게 오면 APPLIED 가 된다")
    void failedChunkBecomesAppliedOnLateSuccess() {
        Chunk chunk = Chunk.sent(0);
        chunk.recordFailure(1);

        chunk.recordSuccess();

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }

    @Test
    @DisplayName("APPLIED 인 청크에 실패 결과가 와도 그대로다")
    void appliedChunkIgnoresFailure() {
        Chunk chunk = Chunk.sent(0);
        chunk.recordSuccess();

        chunk.recordFailure(1);

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }

    @Test
    @DisplayName("실패한 청크가 아니면 재전송할 수 없다")
    void onlyFailedChunkCanBeRetried() {
        Chunk chunk = Chunk.sent(0);
        chunk.recordSuccess();

        assertThrows(DeploymentDomainException.class, chunk::retry);
    }

}
