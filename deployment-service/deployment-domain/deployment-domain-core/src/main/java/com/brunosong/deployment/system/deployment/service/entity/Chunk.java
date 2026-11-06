package com.brunosong.deployment.system.deployment.service.entity;

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
