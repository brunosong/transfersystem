package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeploymentRepository {

    Deployment save(Deployment deployment);

    Optional<Deployment> findById(UUID deploymentId);

    /** 이 시각 전에 보낸 뒤 아직 응답을 기다리는 청크가 있는 배포의 ID */
    List<UUID> findIdsWithChunksSentBefore(Instant sentBefore);

    Optional<List<Deployment>> findAll();

    void updateDeploymentSendResult(Deployment deployment);

}
