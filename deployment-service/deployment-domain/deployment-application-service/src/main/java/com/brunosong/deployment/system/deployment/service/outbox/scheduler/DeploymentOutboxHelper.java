package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.saga.SagaStatus;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brunosong.deployment.system.saga.deployment.SagaConstants.DEPLOYMENT_SAGA_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentOutboxHelper {

    private final DeploymentOutboxRepository deploymentOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<List<DeploymentOutboxMessage>> getDeploymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                                         SagaStatus... sagaStatus) {
        return deploymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(DEPLOYMENT_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<DeploymentOutboxMessage> getDeploymentOutboxMessageBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return deploymentOutboxRepository.findByTypeSagaIdAndSagaStatus(DEPLOYMENT_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(DeploymentOutboxMessage deploymentOutboxMessage) {

        DeploymentOutboxMessage response = deploymentOutboxRepository.save(deploymentOutboxMessage);

        if (response == null) {
            log.error("Failed to save DeploymentOutboxMessage: {}", deploymentOutboxMessage);
            throw new DeploymentDomainException("Failed to save DeploymentOutboxMessage: " + deploymentOutboxMessage);
        } else {
            log.info("DeploymentOutboxMessage saved successfully: {}", response);
        }

    }

    @Transactional
    public void saveDeploymentOutboxMessage(DeploymentEventPayload deploymentEventPayload, UUID sagaId) {
        save(DeploymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .type(DEPLOYMENT_SAGA_NAME)
                .transType(deploymentEventPayload.getTransType())
                .sagaId(sagaId)
                .payload(createPayload(deploymentEventPayload))
                .createdAt(deploymentEventPayload.getCreatedAt())
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .build());
    }

    private String createPayload(DeploymentEventPayload deploymentEventPayload) {
        try {
            return objectMapper.writeValueAsString(deploymentEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create JSON payload for DeploymentEventPayload: {}", deploymentEventPayload, e);
            throw new DeploymentDomainException("Could not create JSON payload for DeploymentEventPayload: " + deploymentEventPayload, e);
        }
    }

}
