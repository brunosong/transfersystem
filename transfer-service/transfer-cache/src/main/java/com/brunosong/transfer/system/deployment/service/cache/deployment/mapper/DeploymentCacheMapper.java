package com.brunosong.transfer.system.deployment.service.cache.deployment.mapper;

import com.brunosong.transfer.system.domain.valueobject.DeploymentId;
import com.brunosong.transfer.system.deployment.service.cache.deployment.entity.DeploymentCacheEntity;
import com.brunosong.transfer.system.deployment.service.entity.Deployment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeploymentCacheMapper {

    public Deployment toDeployment(DeploymentCacheEntity deploymentCacheEntity) {
        return Deployment.builder()
                .id(new DeploymentId(UUID.fromString(deploymentCacheEntity.getDeploymentId())))
                .totalChunkSize(deploymentCacheEntity.getTotalChunkSize())
                .build();
    }

}
