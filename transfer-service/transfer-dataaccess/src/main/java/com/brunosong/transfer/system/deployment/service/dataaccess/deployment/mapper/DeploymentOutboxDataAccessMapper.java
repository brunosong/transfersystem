package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.mapper;

import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.entity.DeploymentOutboxEntity;
import com.brunosong.transfer.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeploymentOutboxDataAccessMapper {

    DeploymentOutboxEntity toDeploymentOutboxEntity(DeploymentOutboxMessage deploymentOutboxMessage);
    DeploymentOutboxMessage toDeploymentOutboxMessage(DeploymentOutboxEntity deploymentOutboxEntity);
}
