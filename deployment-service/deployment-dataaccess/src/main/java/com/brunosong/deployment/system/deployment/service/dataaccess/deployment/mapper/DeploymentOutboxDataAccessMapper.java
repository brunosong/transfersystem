package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentOutboxEntity;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeploymentOutboxDataAccessMapper {

    DeploymentOutboxEntity toDeploymentOutboxEntity(DeploymentOutboxMessage deploymentOutboxMessage);
    DeploymentOutboxMessage toDeploymentOutboxMessage(DeploymentOutboxEntity deploymentOutboxEntity);
}
