package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.Failure;
import com.brunosong.deployment.system.deployment.service.valueobject.FailureKind;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

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

    public static Chunk sent(int no, Instant sentAt) {
        return new Chunk(no, ChunkStatus.SENT, 1, sentAt);
    }

    public void recordSuccess() {
        this.status = ChunkStatus.APPLIED;
        this.failure = null;
    }

    public void recordFailure(int attempt, String message, Instant occurredAt) {
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

    public void retry(Instant now) {
        if (this.status != ChunkStatus.FAILED) {
            throw new DeploymentDomainException("Cannot retry chunk " + no + " from " + status);
        }
        this.status = ChunkStatus.SENT;
        this.attempt++;
        this.sentAt = now;
        this.failure = null;
    }

    public void expireIfOverdue(Instant now, Duration deadline) {
        if (this.status == ChunkStatus.SENT && now.isAfter(this.sentAt.plus(deadline))) {
            this.status = ChunkStatus.FAILED;
            this.failure = Failure.noReply(now);
        }
    }

}
