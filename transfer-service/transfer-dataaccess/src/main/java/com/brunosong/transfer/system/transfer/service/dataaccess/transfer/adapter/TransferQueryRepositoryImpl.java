package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.source.repository.SourceConfigJpaRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferChunksJpaRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferChunksJpaRepository.ChunkStatusCount;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferLogJpaRepository;
import com.brunosong.transfer.system.transfer.service.dto.query.ChunkRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.PageView;
import com.brunosong.transfer.system.transfer.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSearchCondition;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferQueryRepository;
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
public class TransferQueryRepositoryImpl implements TransferQueryRepository {

    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";

    private final TransferLogJpaRepository transferLogJpaRepository;
    private final TransferChunksJpaRepository transferChunksJpaRepository;
    private final SourceConfigJpaRepository sourceConfigJpaRepository;

    @Override
    public PageView<TransferRecord> findTransfers(TransferSearchCondition condition) {
        PageRequest pageable = PageRequest.of(condition.page(), condition.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TransferLogEntity> page = condition.status() == null
                ? transferLogJpaRepository.findAll(pageable)
                : transferLogJpaRepository.findAllByTransferStatus(condition.status(), pageable);

        Map<UUID, ChunkCounts> counts = countChunks(page.getContent().stream().map(TransferLogEntity::getId).toList());
        List<TransferRecord> items = page.getContent().stream()
                .map(entity -> toRecord(entity, counts.getOrDefault(entity.getId(), ChunkCounts.EMPTY)))
                .toList();
        return new PageView<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public Optional<TransferRecord> findTransfer(UUID transferId) {
        return transferLogJpaRepository.findById(transferId)
                .map(entity -> toRecord(entity, countChunks(List.of(transferId))
                        .getOrDefault(transferId, ChunkCounts.EMPTY)));
    }

    @Override
    public List<ChunkRecord> findChunks(UUID transferId) {
        return transferChunksJpaRepository.findAllByLog_IdOrderByChunkOffsetAsc(transferId).stream()
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
    public Map<TransferStatus, Long> countByStatus() {
        Map<TransferStatus, Long> result = new EnumMap<>(TransferStatus.class);
        transferLogJpaRepository.countByStatus().forEach(row -> result.put(row.getStatus(), row.getTotal()));
        return result;
    }

    private Map<UUID, ChunkCounts> countChunks(List<UUID> transferIds) {
        Map<UUID, ChunkCounts> result = new HashMap<>();
        if (transferIds.isEmpty()) {
            return result;
        }
        for (ChunkStatusCount row : transferChunksJpaRepository.countByStatus(transferIds)) {
            ChunkCounts current = result.getOrDefault(row.getTransferId(), ChunkCounts.EMPTY);
            result.put(row.getTransferId(), current.add(row.getStatus(), row.getTotal()));
        }
        return result;
    }

    private static TransferRecord toRecord(TransferLogEntity entity, ChunkCounts counts) {
        return new TransferRecord(
                entity.getId(),
                entity.getSourceId(),
                entity.getSourceType() == null ? null : entity.getSourceType().name(),
                entity.getSourceconfigId(),
                entity.getDataMigrationInfoId(),
                entity.getTransferStatus(),
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
