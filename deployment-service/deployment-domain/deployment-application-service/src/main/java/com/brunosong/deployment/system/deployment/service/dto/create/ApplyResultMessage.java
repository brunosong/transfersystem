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
    /** 몇 번째 시도의 결과인가. 실패 결과가 지금 시도의 것인지 가릴 때 쓴다 */
    private int attempt;
    private String message;
    private int totalChunkSize;

    public void updateTotalChunkSize(int totalChunkSize) {
        this.totalChunkSize = totalChunkSize;
    }
}
