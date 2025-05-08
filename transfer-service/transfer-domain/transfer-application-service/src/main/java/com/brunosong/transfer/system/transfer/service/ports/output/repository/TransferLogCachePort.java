package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;

import java.util.Optional;

public interface TransferLogCachePort {
    Optional<Transfer> getTransferInfo(String transferId);
    void saveTransferId(String transferId, int totalChunkSize);
}
