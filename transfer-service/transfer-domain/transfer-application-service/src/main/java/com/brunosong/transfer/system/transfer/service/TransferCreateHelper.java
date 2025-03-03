package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TransferCreateHelper {

    private final TransferLogRepository transferLogRepository;
    private final TransferDataMapper transferDataMapper;

    public TransferCreateHelper(TransferLogRepository transferLogRepository,
                                TransferDataMapper transferDataMapper) {
        this.transferLogRepository = transferLogRepository;
        this.transferDataMapper = transferDataMapper;
    }

    @Transactional
    public Transfer persistTransferLog(Transfer transfer) {
        return saveTransferLog(transfer);
    }

    private Transfer saveTransferLog(Transfer transfer) {
        Transfer transferResult = transferLogRepository.save(transfer);
        if (transferResult == null) {
            log.error("Could not save transferLog!");
            throw new TransferDomainException("Could not save transferLog!");
        }
        return transferResult;
    }
}
