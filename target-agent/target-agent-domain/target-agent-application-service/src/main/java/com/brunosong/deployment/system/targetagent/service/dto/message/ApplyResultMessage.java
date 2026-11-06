package com.brunosong.deployment.system.targetagent.service.dto.message;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;

import java.time.Instant;

/** 대상이 알려 주는 사실. 배포 상태는 받는 쪽이 정한다 */
public record ApplyResultMessage(
        String deploymentId,
        int chunkNo,
        int attempt,
        Outcome outcome,
        Instant occurredAt
) {

    public sealed interface Outcome {
    }

    public record Applied(AppliedContent content) implements Outcome {
    }

    public record Rejected(String message) implements Outcome {
    }
}
