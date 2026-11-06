package com.brunosong.deployment.system.deployment.service.valueobject;

import java.time.Instant;

public record Failure(FailureKind kind, String message, Instant occurredAt) {

    public static Failure rejected(String message, Instant occurredAt) {
        return new Failure(FailureKind.REJECTED, message, occurredAt);
    }
    public static Failure noReply(Instant occurredAt) {
        return new Failure(FailureKind.NO_REPLY, null, occurredAt);
    }
}
