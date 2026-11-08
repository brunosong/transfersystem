package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.FailureKind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ChunkTest {

    private static final Instant SENT_AT = Instant.parse("2026-11-06T10:00:00Z");
    private static final Duration DEADLINE = Duration.ofMinutes(10);

    @Test
    @DisplayName("처음 보낸 청크는 SENT 상태이고 시도 번호가 1이다.")
    void newSentChunkHasFirstAttempt() {
        Chunk chunk = Chunk.sent(0, SENT_AT);

        assertEquals(0, chunk.getNo());
        assertEquals(ChunkStatus.SENT, chunk.getStatus());
        assertEquals(1, chunk.getAttempt());
    }

    @Test
    @DisplayName("보낸 청크에 성공 결과가 오면 APPLIED 가 된다")
    void sentChunkBecomesAppliedOnSuccess() {
        Chunk chunk = Chunk.sent(0, SENT_AT);

        chunk.recordSuccess();

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }

    @Test
    @DisplayName("보낸 청크에 현재 시도의 실패 결과가 오면 FAILED가 된다.")
    void sentChunkBecomesFailedOnCurrentAttemptFailure() {
        Chunk chunk = Chunk.sent(0, SENT_AT);

        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        assertEquals(ChunkStatus.FAILED, chunk.getStatus());
    }

    @Test
    @DisplayName("재전송하면 SENT 로 돌아가고 시도 번호가 오른다")
    void retryGoesBackToSentWithNextAttempt() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        chunk.retry(SENT_AT);

        assertEquals(ChunkStatus.SENT, chunk.getStatus());
        assertEquals(2, chunk.getAttempt());
    }

    @Test
    @DisplayName("이전 시도의 실패 결과가 늦게 오면 무시한다")
    void lateFailureOfPreviousAttemptIsIgnored() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        chunk.retry(SENT_AT);

        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        assertEquals(ChunkStatus.SENT, chunk.getStatus());
    }

    @Test
    @DisplayName("실패한 청크에 성공 결과가 늦게 오면 APPLIED 가 된다")
    void failedChunkBecomesAppliedOnLateSuccess() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        chunk.recordSuccess();

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }

    @Test
    @DisplayName("APPLIED 인 청크에 실패 결과가 와도 그대로다")
    void appliedChunkIgnoresFailure() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordSuccess();

        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }

    @Test
    @DisplayName("실패한 청크가 아니면 재전송할 수 없다")
    void onlyFailedChunkCanBeRetried() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordSuccess();


        assertThrows(DeploymentDomainException.class, () -> {
            chunk.retry(SENT_AT);
        } );
    }

    @Test
    @DisplayName("대상이 실패를 알리면 이유는 REJECTED 이고 메시지를 남긴다.")
    void failureResultIsRejectedWithMessage() {
        Chunk chunk = Chunk.sent(0, SENT_AT);

        chunk.recordFailure(1, "학기가 없습니다.", SENT_AT.plusSeconds(1));

        assertEquals(FailureKind.REJECTED, chunk.getFailure().kind());
        assertEquals("학기가 없습니다.", chunk.getFailure().message());
    }

    @Test
    @DisplayName("기한을 넘긴 청크는 FAILED 가 되고 이유는 NO_REPLY 다")
    void overdueChunkBecomesNoReply() {
        Chunk chunk = Chunk.sent(0, SENT_AT);

        chunk.expireIfOverdue(SENT_AT.plus(DEADLINE).plusSeconds(1), DEADLINE);

        assertEquals(ChunkStatus.FAILED, chunk.getStatus());
        assertEquals(FailureKind.NO_REPLY, chunk.getFailure().kind());
    }

    @Test
    @DisplayName("기한 시각까지는 SENT 로 남는다")
    void chunkAtDeadlineStaysSent() {
        Chunk chunk = Chunk.sent(0, SENT_AT);

        chunk.expireIfOverdue(SENT_AT.plus(DEADLINE), DEADLINE);

        assertEquals(ChunkStatus.SENT, chunk.getStatus());
        assertNull(chunk.getFailure());
    }

    @Test
    @DisplayName("APPLIED 인 청크는 기한이 지나도 그대로다")
    void appliedChunkDoesNotExpire() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordSuccess();

        chunk.expireIfOverdue(SENT_AT.plus(DEADLINE).plusSeconds(1), DEADLINE);

        assertEquals(ChunkStatus.APPLIED, chunk.getStatus());
    }

    @Test
    @DisplayName("NO_REPLY 인 청크에 현재 시도의 실패 결과가 오면 이유만 REJECTED 로 바뀐다")
    void lateFailureUpgradesNoReplyToRejected() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        Instant overdue = SENT_AT.plus(DEADLINE).plusSeconds(1);
        chunk.expireIfOverdue(overdue, DEADLINE);

        chunk.recordFailure(1, "학기가 없습니다", overdue.plusSeconds(1));

        assertEquals(ChunkStatus.FAILED, chunk.getStatus());
        assertEquals(FailureKind.REJECTED, chunk.getFailure().kind());
        assertEquals("학기가 없습니다", chunk.getFailure().message());
    }

    @Test
    @DisplayName("이미 REJECTED 인 청크에 같은 실패가 또 오면 처음 메시지를 지킨다")
    void duplicateFailureKeepsFirstMessage() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordFailure(1, "처음 오류", SENT_AT);

        chunk.recordFailure(1, "나중 오류", SENT_AT.plusSeconds(1));

        assertEquals("처음 오류", chunk.getFailure().message());
    }

    @Test
    @DisplayName("재전송하면 실패 이유를 지우고 기한을 새로 잰다")
    void retryClearsFailureAndRestartsDeadline() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordFailure(1, "오류", SENT_AT);
        Instant retriedAt = SENT_AT.plus(DEADLINE);

        chunk.retry(retriedAt);
        chunk.expireIfOverdue(SENT_AT.plus(DEADLINE).plusSeconds(1), DEADLINE);

        assertEquals(ChunkStatus.SENT, chunk.getStatus());
        assertNull(chunk.getFailure());
    }

    @Test
    @DisplayName("늦게 온 성공은 실패 이유를 지운다")
    void lateSuccessClearsFailure() {
        Chunk chunk = Chunk.sent(0, SENT_AT);
        chunk.recordFailure(1, "오류", SENT_AT);

        chunk.recordSuccess();

        assertNull(chunk.getFailure());
    }

    @Test
    @DisplayName("기다리는 청크는 WAITING 이고 아직 시도하지 않았다")
    void waitingChunkHasNoAttempt() {
        Chunk chunk = Chunk.waiting(1);

        assertEquals(ChunkStatus.WAITING, chunk.getStatus());
        assertEquals(0, chunk.getAttempt());
        assertNull(chunk.getSentAt());
    }

    @Test
    @DisplayName("기다리는 청크는 기한 검사에서 빠진다")
    void waitingChunkDoesNotExpire() {
        Chunk chunk = Chunk.waiting(1);

        chunk.expireIfOverdue(SENT_AT.plus(DEADLINE).plusSeconds(1), DEADLINE);

        assertEquals(ChunkStatus.WAITING, chunk.getStatus());
    }

}
