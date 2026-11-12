package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.ChunkJpaRepository;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper.DeploymentDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentChunksJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentJpaRepository;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
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

    private final ChunkJpaRepository chunkJpaRepository;
    private final EntityManager entityManager;

    /**
     * 배포와 청크를 함께 저장한다. 같은 트랜잭션에서 findById 로 읽은 배포를 고쳐 저장하는 것이 기본 쓰임이다.
     * 이미 있는 배포는 버전을 꼭 올린다. 과목 성공처럼 배포 행은 그대로이고 청크만 바뀌는 결과도 있기 때문이다.
     * 버전이 안 오르면 동시에 온 두 결과가 서로의 청크를 못 본 채 저장되어,
     * 청크는 모두 반영됐는데 배포는 계속 진행 중으로 남을 수 있다.
     */
    @Override
    public Deployment save(Deployment deployment) {
        UUID id = deployment.getId().getValue();
        Optional<DeploymentEntity> found = deploymentJpaRepository.findById(id);
        if (found.isPresent()) {
            DeploymentEntity entity = found.get();
            entity.updateDeploymentStatus(deployment.getDeploymentStatus());
            entityManager.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        } else {
            deploymentJpaRepository.save(deploymentDataAccessMapper.deploymentToDeploymentEntity(deployment));
        }
        chunkJpaRepository.saveAll(deploymentDataAccessMapper.chunksToChunkEntities(id, deployment.getChunks()));
        return deployment;
    }

    @Override
    public void saveChunk(ApplyResultMessage applyResultMessage) {

        UUID deploymentId = applyResultMessage.getDeploymentId().getValue();
        int chunkOffset = applyResultMessage.getChunkOffset();

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
                deploymentDataAccessMapper.applyResultMessageToChunkEntity(applyResultMessage, deploymentEntity);

        deploymentChunksJpaRepository.save(deploymentChunkEntity);
    }

    /** 배포를 청크와 함께 되살린다. 청크가 없으면 결과를 처리할 수 없어서 늘 함께 읽는다. */
    @Override
    public Optional<Deployment> findById(UUID deploymentId) {
        return deploymentJpaRepository.findById(deploymentId)
                .map(entity -> deploymentDataAccessMapper.deploymentEntityToDeployment(entity,
                        chunkJpaRepository.findAllById_DeploymentIdOrderById_ChunkNoAsc(deploymentId)));
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
                                                            deployment.getTotalChunkSize() ));
    }
}
