package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper;

import com.brunosong.deployment.system.domain.valueobject.TargetId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import org.springframework.stereotype.Component;


@Component
public class DeploymentDataAccessMapper {

    public DeploymentEntity deploymentToDeploymentEntity(Deployment deployment) {
        return DeploymentEntity.builder()
                .id(deployment.getId().getValue())
                .sourceId(deployment.getSourceId().getValue())
                .sourceType(deployment.getSourceContentData().getSourceType())
                .sourceconfigId(deployment.getSourceConfigId().getValue())
                .targetId(deployment.getTargetId().getValue())
                .transType(deployment.getTransType())
                .deploymentStatus(deployment.getDeploymentStatus())
                .dbType(deployment.getSourceContentData().getDbType())
                .totalChunkSize(deployment.getSourceContentData().getChunkOffset())
                .createAdminId("BrunoSong")
                .build();
    }

    public Deployment deploymentEntityToDeployment(DeploymentEntity entity) {
        return Deployment.builder()
                .id(new DeploymentId(entity.getId()))
                .targetId(new TargetId(entity.getTargetId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .transType(entity.getTransType())
                .deploymentStatus(entity.getDeploymentStatus())
                .totalChunkSize(entity.getTotalChunkSize())
                .build();
    }

    public DeploymentChunkEntity applyResultMessageToChunkEntity(ApplyResultMessage applyResultMessage, DeploymentEntity deploymentEntity) {
        return DeploymentChunkEntity.builder()
                .status(applyResultMessage.getDeploymentStatus().getDescription())
                .chunkOffset(applyResultMessage.getChunkOffset())
                .size(applyResultMessage.getTotalChunkSize())
                .log(deploymentEntity)
                .build();
    }
}
