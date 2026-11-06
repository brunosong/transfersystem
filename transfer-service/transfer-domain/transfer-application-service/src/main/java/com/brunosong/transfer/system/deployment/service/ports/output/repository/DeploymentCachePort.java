package com.brunosong.transfer.system.deployment.service.ports.output.repository;

import com.brunosong.transfer.system.deployment.service.entity.Deployment;

import java.util.Optional;

public interface DeploymentCachePort {
    Optional<Deployment> getDeploymentInfo(String deploymentId);
    void saveDeploymentId(String deploymentId, int totalChunkSize);
}
