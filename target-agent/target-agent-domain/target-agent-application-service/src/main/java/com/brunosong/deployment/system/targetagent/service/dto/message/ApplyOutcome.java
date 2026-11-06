package com.brunosong.deployment.system.targetagent.service.dto.message;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import lombok.Builder;

@Builder
public record ApplyOutcome(
        long id,
        String curriculumId,
        AppliedContent applied
) {
}
