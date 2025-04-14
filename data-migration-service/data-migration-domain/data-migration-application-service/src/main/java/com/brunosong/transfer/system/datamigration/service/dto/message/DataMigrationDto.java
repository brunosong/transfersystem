package com.brunosong.transfer.system.datamigration.service.dto.message;

import lombok.Builder;

@Builder
public record DataMigrationDto(
        long id,
        String curriculumId
) {
}
