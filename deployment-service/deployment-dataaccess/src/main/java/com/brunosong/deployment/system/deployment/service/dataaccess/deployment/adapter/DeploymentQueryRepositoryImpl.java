package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.ChunkJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.source.repository.SourceConfigJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.ChunkJpaRepository.ChunkStatusCount;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSearchCondition;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentQueryRepository;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeploymentQueryRepositoryImpl implements DeploymentQueryRepository {

    private final DeploymentJpaRepository deploymentJpaRepository;
    private final ChunkJpaRepository chunkJpaRepository;
    private final SourceConfigJpaRepository sourceConfigJpaRepository;

    @Override
    public PageView<DeploymentRecord> findDeployments(DeploymentSearchCondition condition) {
        PageRequest pageable = PageRequest.of(condition.page(), condition.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DeploymentEntity> page = condition.status() == null
                ? deploymentJpaRepository.findAll(pageable)
                : deploymentJpaRepository.findAllByDeploymentStatus(condition.status(), pageable);

        Map<UUID, ChunkCounts> counts = countChunks(page.getContent().stream().map(DeploymentEntity::getId).toList());
        List<DeploymentRecord> items = page.getContent().stream()
                .map(entity -> toRecord(entity, counts.getOrDefault(entity.getId(), ChunkCounts.EMPTY)))
                .toList();
        return new PageView<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public Optional<DeploymentRecord> findDeployment(UUID deploymentId) {
        return deploymentJpaRepository.findById(deploymentId)
                .map(entity -> toRecord(entity, countChunks(List.of(deploymentId))
                        .getOrDefault(deploymentId, ChunkCounts.EMPTY)));
    }

    @Override
    public List<ChunkRecord> findChunks(UUID deploymentId) {
        return chunkJpaRepository.findAllById_DeploymentIdOrderById_ChunkNoAsc(deploymentId).stream()
                .map(chunk -> new ChunkRecord(chunk.getId().getChunkNo(),
                        chunk.getStatus().name(),
                        // 실패했으면 실패한 시각, 아니면 보낸 시각. 반영된 시각은 기록하지 않는다
                        chunk.getFailedAt() != null ? chunk.getFailedAt() : chunk.getSentAt(),
                        chunk.getFailureMessage(),
                        chunk.getAttempt()))
                .toList();
    }

    @Override
    public List<SourceConfigView> findSourceConfigs() {
        return sourceConfigJpaRepository.findAllSummaries().stream()
                .map(s -> new SourceConfigView(s.getSourceConfigId(),
                        s.getDatabaseName() + "." + s.getTableOrCollection(), true,
                        s.getSourceType(), s.getDbType(),
                        s.getHost(), s.getPort(), s.getDatabaseName(), s.getTableOrCollection()))
                .toList();
    }

    @Override
    public Map<DeploymentStatus, Long> countByStatus() {
        Map<DeploymentStatus, Long> result = new EnumMap<>(DeploymentStatus.class);
        deploymentJpaRepository.countByStatus().forEach(row -> result.put(row.getStatus(), row.getTotal()));
        return result;
    }

    private Map<UUID, ChunkCounts> countChunks(List<UUID> deploymentIds) {
        Map<UUID, ChunkCounts> result = new HashMap<>();
        if (deploymentIds.isEmpty()) {
            return result;
        }
        for (ChunkStatusCount row : chunkJpaRepository.countByStatus(deploymentIds)) {
            ChunkCounts current = result.getOrDefault(row.getDeploymentId(), ChunkCounts.EMPTY);
            result.put(row.getDeploymentId(), current.add(row.getStatus(), row.getTotal()));
        }
        return result;
    }

    private static DeploymentRecord toRecord(DeploymentEntity entity, ChunkCounts counts) {
        return new DeploymentRecord(
                entity.getId(),
                entity.getSourceId(),
                entity.getSourceType() == null ? null : entity.getSourceType().name(),
                entity.getSourceconfigId(),
                entity.getTargetId(),
                entity.getDeploymentStatus(),
                entity.getTotalChunkSize(),
                counts.succeeded(),
                counts.failed(),
                entity.getCreateAdminId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getResultMessage());
    }

    private record ChunkCounts(long succeeded, long failed) {
        static final ChunkCounts EMPTY = new ChunkCounts(0, 0);

        ChunkCounts add(ChunkStatus status, long total) {
            return switch (status) {
                case APPLIED -> new ChunkCounts(succeeded + total, failed);
                case FAILED -> new ChunkCounts(succeeded, failed + total);
                default -> this;
            };
        }
    }
}
