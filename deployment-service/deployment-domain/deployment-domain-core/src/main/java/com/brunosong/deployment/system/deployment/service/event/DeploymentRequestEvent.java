package com.brunosong.deployment.system.deployment.service.event;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class DeploymentRequestEvent extends DeploymentEvent {

    private final int chunkOffset;
    private final ChunkContent content;

    public DeploymentRequestEvent(Deployment deployment,
                                ZonedDateTime createdAt, int chunkOffset, ChunkContent content) {
        super(deployment, createdAt);
        this.chunkOffset = chunkOffset;
        this.content = content;
    }

}
