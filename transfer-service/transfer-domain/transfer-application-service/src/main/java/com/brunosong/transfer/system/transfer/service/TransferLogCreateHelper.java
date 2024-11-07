package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferLogCreateHelper {

    private final TransferLogRepository transferLogRepository;
    private final TransferDataMapper transferDataMapper;

    public TransferLogCreateHelper(TransferLogRepository transferLogRepository,
                                   TransferDataMapper transferDataMapper) {
        this.transferLogRepository = transferLogRepository;
        this.transferDataMapper = transferDataMapper;
    }

    public TransferLog persistTransferLog(ExecutionTransferCommand command) {
        TransferLog transferLog = transferDataMapper.executionTransferCommandToTransferLog(command);
        transferLog.initializeTransferLog();
        return saveTransferLog(transferLog);
    }

    private TransferLog saveTransferLog(TransferLog transferLog) {
        TransferLog transferLogResult = transferLogRepository.save(transferLog);
        if (transferLogResult == null) {
            log.error("Could not save transferLog!");
            throw new TransferDomainException("Could not save transferLog!");
        }
        return transferLogResult;
    }
}
