package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import com.brunosong.deployment.system.deployment.service.entity.Chunk;
import com.brunosong.deployment.system.deployment.service.valueobject.Failure;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
public class DeploymentDataAccessMapper {

    public DeploymentEntity deploymentToDeploymentEntity(Deployment deployment) {
        return DeploymentEntity.builder()
                .id(deployment.getId().getValue())
                .sourceId(deployment.getSourceId().getValue())
                .sourceType(deployment.getSourceType())
                .sourceconfigId(deployment.getSourceConfigId().getValue())
                .curriculumId(deployment.getCurriculumId())
                .targetId(deployment.getTargetId().getValue())
                .transType(deployment.getTransType())
                .deploymentStatus(deployment.getDeploymentStatus())
                .dbType(deployment.getDbType())
                .totalChunkSize(deployment.getTotalChunkSize())
                .createAdminId("BrunoSong")
                .build();
    }

    public DeploymentChunkEntity applyResultMessageToChunkEntity(ApplyResultMessage applyResultMessage, DeploymentEntity deploymentEntity) {
        return DeploymentChunkEntity.builder()
                .status(applyResultMessage.getDeploymentStatus().getDescription())
                .chunkOffset(applyResultMessage.getChunkOffset())
                .size(applyResultMessage.getTotalChunkSize())
                .log(deploymentEntity)
                .build();
    }

    /** 청크 없이 배포만 되살린다. 목록 조회처럼 청크가 필요 없는 곳에서 쓴다. */
    public Deployment deploymentEntityToDeployment(DeploymentEntity entity) {
        return deploymentEntityToDeployment(entity, List.of());
    }

    /** 배포 행과 청크 행으로 배포를 되살린다. 결과를 처리할 때는 이것으로 읽어야 규칙이 지켜진다. */
    public Deployment deploymentEntityToDeployment(DeploymentEntity entity, List<ChunkEntity> chunkEntities) {
        return Deployment.builder()
                .id(new DeploymentId(entity.getId()))
                .targetId(new TargetId(entity.getTargetId()))
                .curriculumId(entity.getCurriculumId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .transType(entity.getTransType())
                .deploymentStatus(entity.getDeploymentStatus())
                .totalChunkSize(entity.getTotalChunkSize())
                .chunks(chunkEntities.stream().map(this::chunkEntityToChunk).toList())
                .build();
    }

    /** 도메인 청크를 행으로 바꾼다. 실패 이유(Failure)는 세 칸으로 풀어 저장한다. */
    public List<ChunkEntity> chunksToChunkEntities(UUID deploymentId, List<Chunk> chunks) {
        return chunks.stream().map(chunk -> chunkToChunkEntity(deploymentId, chunk)).toList();
    }

    private ChunkEntity chunkToChunkEntity(UUID deploymentId, Chunk chunk) {
        Failure failure = chunk.getFailure();
        return ChunkEntity.builder()
                .id(new ChunkEntityId(deploymentId, chunk.getNo()))
                .status(chunk.getStatus())
                .attempt(chunk.getAttempt())
                .sentAt(chunk.getSentAt())
                .failureKind(failure == null ? null : failure.kind())
                .failureMessage(failure == null ? null : failure.message())
                .failedAt(failure == null ? null : failure.occurredAt())
                .build();
    }

    /** 행을 도메인 청크로 되살린다. 실패 종류 칸이 비어 있으면 실패가 없는 것이다. */
    private Chunk chunkEntityToChunk(ChunkEntity entity) {
        Failure failure = entity.getFailureKind() == null
                ? null
                : new Failure(entity.getFailureKind(), entity.getFailureMessage(), entity.getFailedAt());
        return Chunk.restore(entity.getId().getChunkNo(), entity.getStatus(), entity.getAttempt(),
                entity.getSentAt(), failure);
    }

}
