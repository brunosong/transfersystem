package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.Failure;
import com.brunosong.deployment.system.deployment.service.valueobject.FailureKind;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

/**
 * 배포 하나를 나눈 조각. 0번은 뼈대(학년, 학기), 1번부터는 과목이다.
 * 상태를 바꾸는 메서드는 패키지 안에서만 부를 수 있다. 애그리거트 루트인 Deployment 를 거쳐야 한다.
 */
@Getter
public class Chunk {

    private final int no;
    private ChunkStatus status;
    private int attempt;
    private Instant sentAt;
    private Failure failure;

    private Chunk(int no, ChunkStatus status, int attempt, Instant sentAt) {
        this.no = no;
        this.status = status;
        this.attempt = attempt;
        this.sentAt = sentAt;
    }

    /** 바로 보내는 청크를 만든다. 첫 시도이고, 보낸 시각부터 응답 기한을 잰다. */
    static Chunk sent(int no, Instant sentAt) {
        return new Chunk(no, ChunkStatus.SENT, 1, sentAt);
    }

    /** 뼈대가 반영되기를 기다리는 청크를 만든다. 아직 보내지 않아 시도 번호가 0이고 보낸 시각이 없다. */
    static Chunk waiting(int no) {
        return new Chunk(no, ChunkStatus.WAITING, 0, null);
    }

    /**
     * 대상이 반영했다는 결과를 기록한다.
     * 어느 시도의 성공이든 APPLIED 로 바꾼다. 한 배포의 청크 내용은 시도와 상관없이 같기 때문이다.
     */
    void recordSuccess() {
        this.status = ChunkStatus.APPLIED;
        this.failure = null;
    }

    /**
     * 대상이 실패했다는 결과를 기록한다.
     * 지금 시도의 결과일 때만 반영한다. 이전 시도의 늦은 결과와 APPLIED 인 청크는 무시한다.
     * 응답이 없어 실패(NO_REPLY)했던 청크라면 상태는 그대로 두고 이유만 REJECTED 로 바꾼다.
     */
    void recordFailure(int attempt, String message, Instant occurredAt) {
        if (this.attempt != attempt) {
            return;
        }
        if (this.status == ChunkStatus.SENT) {
            this.status = ChunkStatus.FAILED;
            this.failure = Failure.rejected(message, occurredAt);
        } else if (this.status == ChunkStatus.FAILED && this.failure.kind() == FailureKind.NO_REPLY) {
            this.failure = Failure.rejected(message, occurredAt);
        }
    }

    /**
     * 실패한 청크를 다시 보낸다. 시도 번호를 올리고, 실패 이유를 지우고, 응답 기한을 새로 잰다.
     * 실패한 청크가 아니면 예외를 던진다. 재전송은 운영자의 요청이라 안 되는 이유를 알려야 한다.
     */
    void retry(Instant now) {
        if (this.status != ChunkStatus.FAILED) {
            throw new DeploymentDomainException("Cannot retry chunk " + no + " from " + status);
        }
        this.status = ChunkStatus.SENT;
        this.attempt++;
        this.sentAt = now;
        this.failure = null;
    }

    /**
     * 보낸 뒤 기한이 지나도록 응답이 없으면 FAILED(NO_REPLY) 로 바꾼다.
     * SENT 인 청크만 본다. 기한 시각과 같으면 아직 지나지 않은 것으로 본다.
     */
    void expireIfOverdue(Instant now, Duration deadline) {
        if (this.status == ChunkStatus.SENT && now.isAfter(this.sentAt.plus(deadline))) {
            this.status = ChunkStatus.FAILED;
            this.failure = Failure.noReply(now);
        }
    }

}
