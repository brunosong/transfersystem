package com.brunosong.transfer.system.datamigration.service.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateLearningMetaCommand {

    @NotNull
    private UUID dataMigrationInfoId;

    @NotNull
    private LearningMaterial learningMaterial;

}
