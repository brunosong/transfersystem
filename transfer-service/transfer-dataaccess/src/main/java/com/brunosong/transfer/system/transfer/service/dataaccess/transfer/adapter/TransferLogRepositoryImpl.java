package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferChunkEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferLogDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferChunksJpaRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferLogJpaRepository;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
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
public class TransferLogRepositoryImpl implements TransferLogRepository {

    private final TransferLogJpaRepository transferLogJpaRepository;
    private final TransferChunksJpaRepository transferChunksJpaRepository;
    private final TransferLogDataAccessMapper transferLogDataAccessMapper;

    @Override
    public Transfer save(Transfer transfer) {

        TransferLogEntity transferLogEntity =
                transferLogDataAccessMapper.transferLogToTransferLogEntity(transfer);

        TransferLogEntity save = transferLogJpaRepository.save(transferLogEntity);

        return transferLogDataAccessMapper.transferLogEntityToTransferLog(save);
    }

    @Override
    public void saveChunk(DataMigrationResponse dataMigrationResponse) {

        UUID transferLogId = dataMigrationResponse.getTransferId().getValue();
        int chunkOffset = dataMigrationResponse.getChunkOffset();

        // 메시지는 최소 한 번 전달된다. 아웃박스가 보내고 상태를 닫기 전에 죽으면
        // 다음 주기가 같은 것을 다시 보낸다. 중복은 사고가 아니라 정상 경로다
        if (transferChunksJpaRepository.existsByLog_IdAndChunkOffset(transferLogId, chunkOffset)) {
            log.info("이미 저장된 청크라 건너뜁니다. transferId={} chunkOffset={}", transferLogId, chunkOffset);
            return;
        }

        TransferLogEntity transferLogEntity = TransferLogEntity.builder()
                .id(transferLogId)
                .build();

        TransferChunkEntity transferChunkEntity =
                transferLogDataAccessMapper.dataMigrationResponseToChunkEntity(dataMigrationResponse, transferLogEntity);

        transferChunksJpaRepository.save(transferChunkEntity);
    }

    @Override
    public Optional<Transfer> findById(UUID transferLogId) {
        return transferLogJpaRepository.findById(transferLogId)
                .map(transferLogDataAccessMapper::transferLogEntityToTransferLog);
    }

    @Override
    public Optional<List<Transfer>> findAll() {
        return Optional.of(transferLogJpaRepository.findAll().stream().map(transferLogDataAccessMapper::transferLogEntityToTransferLog)
                .collect(Collectors.toList()));
    }

    @Override
    public void updateTransferStatus(Transfer transfer) {
        Optional<TransferLogEntity> result = transferLogJpaRepository.findById(transfer.getId().getValue());
        result.ifPresent(entity -> entity.updateResult( transfer.getTransferStatus(),
                                                        transfer.getResultMessage() ));
    }

    @Override
    public void updateTransferSendResult(Transfer transfer) {
        Optional<TransferLogEntity> result = transferLogJpaRepository.findById(transfer.getId().getValue());
        result.ifPresent(entity -> entity.updateSendResult( transfer.getTransferStatus(),
                                                            transfer.getSourceContentData().getChunkOffset() ));
    }
}
