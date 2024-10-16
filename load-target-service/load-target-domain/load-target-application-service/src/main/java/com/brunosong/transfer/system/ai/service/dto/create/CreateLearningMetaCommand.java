package com.brunosong.transfer.system.ai.service.dto.create;

import com.brunosong.transfer.system.ai.service.domain.entity.LearningMaterial;
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
    private UUID empId;
    @NotNull
    private boolean isReal;
    @NotNull
    private LearningMaterial learningMaterial;

}
