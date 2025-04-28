package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferLogRepository {

    Transfer save(Transfer transfer);

    void saveChunk(DataMigrationResponse dataMigrationResponse);

    Optional<Transfer> findById(UUID transferLogId);

    Optional<List<Transfer>> findAll();

    void updateTransferStatus(Transfer transfer);

    void updateTransferSendResult(Transfer transfer);

}
