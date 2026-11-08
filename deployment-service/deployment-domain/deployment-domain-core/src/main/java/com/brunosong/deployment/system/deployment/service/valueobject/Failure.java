package com.brunosong.deployment.system.deployment.service.valueobject;

import java.time.Instant;

/** 청크가 반영되지 않은 이유. 종류, 대상이 보낸 메시지, 실패로 기록한 시각을 담는다. */
public record Failure(FailureKind kind, String message, Instant occurredAt) {

    /** 대상이 실패를 알려 왔다. 대상이 보낸 오류 메시지를 남긴다. */
    public static Failure rejected(String message, Instant occurredAt) {
        return new Failure(FailureKind.REJECTED, message, occurredAt);
    }

    /** 기한 안에 응답이 없었다. 대상이 보낸 것이 없으므로 메시지가 없다. */
    public static Failure noReply(Instant occurredAt) {
        return new Failure(FailureKind.NO_REPLY, null, occurredAt);
    }
}
