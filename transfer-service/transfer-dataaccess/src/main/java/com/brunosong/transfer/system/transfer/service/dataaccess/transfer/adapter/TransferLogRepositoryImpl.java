package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferLogDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferLogJpaRepository;
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
    private final TransferLogDataAccessMapper transferLogDataAccessMapper;

    @Override
    public Transfer save(Transfer transfer) {
        System.out.println("Transfer ID: " + transfer.getId()); // ID 출력
        TransferLogEntity transferLogEntity = transferLogDataAccessMapper.transferLogToTransferLogEntity(transfer);
        System.out.println("Before save - Entity ID: " + transferLogEntity.getId());
        TransferLogEntity save = transferLogJpaRepository.save(transferLogEntity);
        System.out.println("After save - Entity ID: " + save.getId());
        return transferLogDataAccessMapper.transferLogEntityToTransferLog(save);
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
    @Transactional
    public void updateTransferStatus(UUID transferId, TransferStatus transferStatus) {
        Optional<TransferLogEntity> result = transferLogJpaRepository.findById(transferId);
        result.ifPresent(entity -> entity.updateTransferStatus(transferStatus));
    }
}
