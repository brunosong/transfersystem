package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.util.List;
import java.util.Optional;

public interface DeploymentOutboxRepository {

    DeploymentOutboxMessage save(DeploymentOutboxMessage deploymentOutboxMessage);

    /** 넣은 순서(seq)대로, 한 번에 100개까지 */
    Optional<List<DeploymentOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
