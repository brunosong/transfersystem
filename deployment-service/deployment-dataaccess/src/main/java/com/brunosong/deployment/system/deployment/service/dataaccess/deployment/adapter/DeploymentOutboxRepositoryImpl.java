package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentOutboxEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper.DeploymentOutboxDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentOutboxJpaRepository;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    public Optional<List<DeploymentOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        return deploymentOutboxJpaRepository
                .findByTypeAndOutboxStatus(type, outboxStatus)
                .map(entities -> entities.stream()
                        .map(deploymentOutboxDataAccessMapper::toDeploymentOutboxMessage)
                        .toList());
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        deploymentOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
    }
}
