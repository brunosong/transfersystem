package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferLogRepository {

    Transfer save(Transfer transfer);

    Optional<Transfer> findById(UUID transferLogId);

    Optional<List<Transfer>> findAll();

}
