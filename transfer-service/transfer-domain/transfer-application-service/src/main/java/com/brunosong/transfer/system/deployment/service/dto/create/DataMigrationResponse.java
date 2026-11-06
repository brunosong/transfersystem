package com.brunosong.transfer.system.deployment.service.dto.create;

import com.brunosong.transfer.system.domain.valueobject.DeploymentId;
import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMigrationResponse {
    private DeploymentId deploymentId;
    private DeploymentStatus deploymentStatus;
    private int chunkOffset;
    private String message;
    private int totalChunkSize;

    public void updateTotalChunkSize(int totalChunkSize) {
        this.totalChunkSize = totalChunkSize;
    }
}
