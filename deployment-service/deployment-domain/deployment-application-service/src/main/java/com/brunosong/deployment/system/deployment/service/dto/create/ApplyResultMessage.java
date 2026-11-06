package com.brunosong.deployment.system.deployment.service.dto.create;

import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyResultMessage {
    private DeploymentId deploymentId;
    private DeploymentStatus deploymentStatus;
    private int chunkOffset;
    private String message;
    private int totalChunkSize;

    public void updateTotalChunkSize(int totalChunkSize) {
        this.totalChunkSize = totalChunkSize;
    }
}
