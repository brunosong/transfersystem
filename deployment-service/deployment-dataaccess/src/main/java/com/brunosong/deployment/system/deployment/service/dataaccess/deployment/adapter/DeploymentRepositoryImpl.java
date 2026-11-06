package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper.DeploymentDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentChunksJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentJpaRepository;
import com.brunosong.deployment.system.deployment.service.dto.create.DataMigrationResponse;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentRepositoryImpl implements DeploymentRepository {

    private final DeploymentJpaRepository deploymentJpaRepository;
    private final DeploymentChunksJpaRepository deploymentChunksJpaRepository;
    private final DeploymentDataAccessMapper deploymentDataAccessMapper;

    @Override
    public Deployment save(Deployment deployment) {

        DeploymentEntity deploymentEntity =
                deploymentDataAccessMapper.deploymentToDeploymentEntity(deployment);

        DeploymentEntity save = deploymentJpaRepository.save(deploymentEntity);

        return deploymentDataAccessMapper.deploymentEntityToDeployment(save);
    }

    @Override
    public void saveChunk(DataMigrationResponse dataMigrationResponse) {

        UUID deploymentId = dataMigrationResponse.getDeploymentId().getValue();
        int chunkOffset = dataMigrationResponse.getChunkOffset();

        // 메시지는 최소 한 번 전달된다. 아웃박스가 보내고 상태를 닫기 전에 죽으면
        // 다음 주기가 같은 것을 다시 보낸다. 중복은 사고가 아니라 정상 경로다
        if (deploymentChunksJpaRepository.existsByLog_IdAndChunkOffset(deploymentId, chunkOffset)) {
            log.info("이미 저장된 청크라 건너뜁니다. deploymentId={} chunkOffset={}", deploymentId, chunkOffset);
            return;
        }

        DeploymentEntity deploymentEntity = DeploymentEntity.builder()
                .id(deploymentId)
                .build();

        DeploymentChunkEntity deploymentChunkEntity =
                deploymentDataAccessMapper.dataMigrationResponseToChunkEntity(dataMigrationResponse, deploymentEntity);

        deploymentChunksJpaRepository.save(deploymentChunkEntity);
    }

    @Override
    public Optional<Deployment> findById(UUID deploymentId) {
        return deploymentJpaRepository.findById(deploymentId)
                .map(deploymentDataAccessMapper::deploymentEntityToDeployment);
    }

    @Override
    public Optional<List<Deployment>> findAll() {
        return Optional.of(deploymentJpaRepository.findAll().stream().map(deploymentDataAccessMapper::deploymentEntityToDeployment)
                .collect(Collectors.toList()));
    }

    @Override
    public void updateDeploymentStatus(Deployment deployment) {
        Optional<DeploymentEntity> result = deploymentJpaRepository.findById(deployment.getId().getValue());
        result.ifPresent(entity -> entity.updateResult( deployment.getDeploymentStatus(),
                                                        deployment.getResultMessage() ));
    }

    @Override
    public void updateDeploymentSendResult(Deployment deployment) {
        Optional<DeploymentEntity> result = deploymentJpaRepository.findById(deployment.getId().getValue());
        result.ifPresent(entity -> entity.updateSendResult( deployment.getDeploymentStatus(),
                                                            deployment.getSourceContentData().getChunkOffset() ));
    }
}
