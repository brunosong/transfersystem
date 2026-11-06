package com.brunosong.transfer.system.deployment.service.ports.input.service;

import com.brunosong.transfer.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.transfer.system.deployment.service.dto.create.CreateDeploymentResponse;

public interface DeploymentApplicationService {

    CreateDeploymentResponse executeDeployment(DeploymentRequest deploymentRequest);

}
