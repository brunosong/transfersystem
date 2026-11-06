package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;

public class Chunk {

    private final int no;
    private ChunkStatus status;
    private int attempt;

    private Chunk(int no, ChunkStatus status, int attempt) {
        this.no = no;
        this.status = status;
        this.attempt = attempt;
    }

    public static Chunk sent(int no) {
        return new Chunk(no, ChunkStatus.SENT, 1);
    }

    public void recordSuccess() {
        this.status = ChunkStatus.APPLIED;
    }

    public void recordFailure(int attempt) {
        if (this.status == ChunkStatus.SENT && this.attempt == attempt) {
            this.status = ChunkStatus.FAILED;
        }
    }

    public void retry() {
        if (this.status != ChunkStatus.FAILED) {
            throw new DeploymentDomainException("Cannot retry chunk " + no + " from " + status);
        }
        this.status = ChunkStatus.SENT;
        this.attempt++;
    }

    public int getNo() {
        return no;
    }

    public ChunkStatus getStatus() {
        return status;
    }

    public int getAttempt() {
        return attempt;
    }
}
