package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.util.List;
import java.util.Optional;

public interface DeploymentOutboxRepository {

    DeploymentOutboxMessage save(DeploymentOutboxMessage deploymentOutboxMessage);

    Optional<List<DeploymentOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
