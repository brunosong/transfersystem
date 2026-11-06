package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.source.repository.SourceConfigJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentChunksJpaRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentChunksJpaRepository.ChunkStatusCount;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentJpaRepository;
import com.brunosong.deployment.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSearchCondition;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeploymentQueryRepositoryImpl implements DeploymentQueryRepository {

    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";

    private final DeploymentJpaRepository deploymentJpaRepository;
    private final DeploymentChunksJpaRepository deploymentChunksJpaRepository;
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
        return deploymentChunksJpaRepository.findAllByLog_IdOrderByChunkOffsetAsc(deploymentId).stream()
                .map(chunk -> new ChunkRecord(chunk.getChunkOffset(),
                        chunk.getStatus().toUpperCase(Locale.ROOT),
                        chunk.getCreatedAt()))
                .toList();
    }

    @Override
    public List<SourceConfigView> findSourceConfigs() {
        return sourceConfigJpaRepository.findAllSummaries().stream()
                .map(s -> new SourceConfigView(s.getSourceConfigId(), s.getSourceType(), s.getDbType(),
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
        for (ChunkStatusCount row : deploymentChunksJpaRepository.countByStatus(deploymentIds)) {
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
                entity.getDataMigrationInfoId(),
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

        ChunkCounts add(String status, long total) {
            return switch (status) {
                case SUCCESS -> new ChunkCounts(succeeded + total, failed);
                case FAILED -> new ChunkCounts(succeeded, failed + total);
                default -> this;
            };
        }
    }
}
