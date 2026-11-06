package com.brunosong.deployment.system.deployment.service.ports.input.service;

import com.brunosong.deployment.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.deployment.system.deployment.service.dto.create.CreateDeploymentResponse;

public interface DeploymentApplicationService {

    CreateDeploymentResponse executeDeployment(DeploymentRequest deploymentRequest);

}
