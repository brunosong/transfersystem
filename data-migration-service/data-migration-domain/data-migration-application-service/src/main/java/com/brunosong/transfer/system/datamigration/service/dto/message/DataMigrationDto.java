package com.brunosong.transfer.system.datamigration.service.dto.message;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.AppliedContent;
import lombok.Builder;

@Builder
public record DataMigrationDto(
        long id,
        String curriculumId,
        AppliedContent applied
) {
}
