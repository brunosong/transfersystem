package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.saga.SagaStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeploymentOutboxRepository {

    DeploymentOutboxMessage save(DeploymentOutboxMessage deploymentOutboxMessage);

    Optional<List<DeploymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                     OutboxStatus outboxStatus,
                                                                                     SagaStatus... sagaStatus);

    Optional<DeploymentOutboxMessage> findByTypeSagaIdAndSagaStatus(String type, UUID sagaId,
                                                                            SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatus);
}
