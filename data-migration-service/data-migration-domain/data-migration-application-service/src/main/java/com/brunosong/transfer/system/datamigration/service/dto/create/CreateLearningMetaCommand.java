package com.brunosong.transfer.system.datamigration.service.dto.create;

import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
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
