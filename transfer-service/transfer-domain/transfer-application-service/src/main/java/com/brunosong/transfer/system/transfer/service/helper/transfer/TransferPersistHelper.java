package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TransferPersistHelper {

    private final TransferLogRepository transferLogRepository;
    private final TransferDataMapper transferDataMapper;

    public TransferPersistHelper(TransferLogRepository transferLogRepository,
                                 TransferDataMapper transferDataMapper) {
        this.transferLogRepository = transferLogRepository;
        this.transferDataMapper = transferDataMapper;
    }

    @Transactional
    public Transfer persistTransferLog(Transfer transfer) {
        return saveTransferLog(transfer);
    }

    @Transactional
    public void updateTransferSendResult(Transfer transfer) {
        transferLogRepository.updateTransferSendResult(transfer);
        log.info("UpdateTransferSendResult transferId is {}", transfer.getId());
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
