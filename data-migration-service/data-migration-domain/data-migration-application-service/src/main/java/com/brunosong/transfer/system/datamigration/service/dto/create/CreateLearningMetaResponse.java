package com.brunosong.transfer.system.datamigration.service.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateLearningMetaResponse {

    @NotNull
    private final String message;

}
