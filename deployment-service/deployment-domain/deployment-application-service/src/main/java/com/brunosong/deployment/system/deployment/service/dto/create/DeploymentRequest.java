package com.brunosong.deployment.system.deployment.service.dto.create;

import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import jakarta.validation.constraints.NotNull;

/**
 * 배포 접수 요청. 컨트롤러와 유스케이스가 함께 쓴다(시스템 설계 2장).
 * record 라서 들어온 요청이 중간에 바뀌지 않는다.
 */
public record DeploymentRequest(
        @NotNull(message = "targetId is null") Long targetId,
        @NotNull(message = "transType is null") TransType transType,
        @NotNull(message = "sourceId is null") String sourceId,
        @NotNull(message = "sourceConfigId is null") Long sourceConfigId) {
}
