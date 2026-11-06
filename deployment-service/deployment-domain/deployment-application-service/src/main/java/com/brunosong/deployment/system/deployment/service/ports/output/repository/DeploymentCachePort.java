package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;

import java.util.Optional;

public interface DeploymentCachePort {
    Optional<Deployment> getDeploymentInfo(String deploymentId);
    void saveDeploymentId(String deploymentId, int totalChunkSize);
}
