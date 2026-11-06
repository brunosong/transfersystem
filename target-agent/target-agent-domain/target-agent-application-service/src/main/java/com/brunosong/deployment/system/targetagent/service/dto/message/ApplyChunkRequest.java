package com.brunosong.deployment.system.targetagent.service.dto.message;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;

public record ApplyChunkRequest(
        String deploymentId,
        int chunkNo,
        int attempt,
        String targetSystem,
        String environment,
        String curriculumId,
        ChunkContent content
) {
}
