package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.entity.DeploymentOutboxEntity;
import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.mapper.DeploymentOutboxDataAccessMapper;
import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.repository.DeploymentOutboxJpaRepository;
import com.brunosong.transfer.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.transfer.system.deployment.service.ports.output.repository.DeploymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentOutboxRepositoryImpl implements DeploymentOutboxRepository {

    private final DeploymentOutboxJpaRepository deploymentOutboxJpaRepository;
    private final DeploymentOutboxDataAccessMapper deploymentOutboxDataAccessMapper;

    @Override
    public DeploymentOutboxMessage save(DeploymentOutboxMessage deploymentOutboxMessage) {
        DeploymentOutboxEntity deploymentOutboxEntity = deploymentOutboxDataAccessMapper.toDeploymentOutboxEntity(deploymentOutboxMessage);
        DeploymentOutboxEntity saveEntity = deploymentOutboxJpaRepository.save(deploymentOutboxEntity);
        return deploymentOutboxDataAccessMapper.toDeploymentOutboxMessage(saveEntity);
    }

    @Override
    public Optional<List<DeploymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                            OutboxStatus outboxStatus,
                                                                                            SagaStatus... sagaStatus) {
        return deploymentOutboxJpaRepository
                .findByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, Arrays.asList(sagaStatus))
                .map(entities -> entities.stream()
                        .map(deploymentOutboxDataAccessMapper::toDeploymentOutboxMessage)
                        .toList());
    }

    @Override
    public Optional<DeploymentOutboxMessage> findByTypeSagaIdAndSagaStatus(String type, UUID sagaId,
                                                                             SagaStatus... sagaStatus) {
        return deploymentOutboxJpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
                .map(deploymentOutboxDataAccessMapper::toDeploymentOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                         OutboxStatus outboxStatus,
                                                         SagaStatus... sagaStatus) {
        deploymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, Arrays.asList(sagaStatus));
    }
}
