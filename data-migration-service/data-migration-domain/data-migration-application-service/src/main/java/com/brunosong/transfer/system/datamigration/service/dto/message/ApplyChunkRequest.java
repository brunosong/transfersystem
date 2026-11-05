package com.brunosong.transfer.system.datamigration.service.dto.message;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.ChunkContent;

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
