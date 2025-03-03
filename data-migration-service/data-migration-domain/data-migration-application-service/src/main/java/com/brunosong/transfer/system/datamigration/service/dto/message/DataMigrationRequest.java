package com.brunosong.transfer.system.datamigration.service.dto.message;

import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataMigrationRequest {
    private String transferId;
    private String dataMigrationId;
    private LearningMaterial learningMaterial;
}
