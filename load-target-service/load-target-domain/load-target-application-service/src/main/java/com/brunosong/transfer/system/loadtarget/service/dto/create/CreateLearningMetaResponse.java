package com.brunosong.transfer.system.loadtarget.service.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class CreateLearningMetaResponse {

    @NotNull
    private final String message;

}
