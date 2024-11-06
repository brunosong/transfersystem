package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.TransferLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferLogRepository {

    TransferLog save(TransferLog transferLog);
    Optional<TransferLog> findById(UUID transferLogId);

    Optional<List<TransferLog>> findAll();

}
