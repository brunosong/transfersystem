package com.brunosong.transfer.system.datamigration.service.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningMaterial {
    @NotNull
    private String title;
    @NotNull
    private String description;
}
