package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.dto.create.DataMigrationResponse;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeploymentRepository {

    Deployment save(Deployment deployment);

    void saveChunk(DataMigrationResponse dataMigrationResponse);

    Optional<Deployment> findById(UUID deploymentId);

    Optional<List<Deployment>> findAll();

    void updateDeploymentStatus(Deployment deployment);

    void updateDeploymentSendResult(Deployment deployment);

}
