package com.brunosong.deployment.system.deployment.service.dto.create;

import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeploymentRequest {

    @NotNull(message = "targetId is null")
    private Long targetId;

    @NotNull(message = "transType is null")
    private TransType transType;

    @NotNull(message = "sourceId is null")
    private String sourceId;

    @NotNull(message = "sourceConfigId is null")
    private Long sourceConfigId;
}
