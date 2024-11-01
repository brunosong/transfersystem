package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class TransferLogCreateHelper {

    private final TransferLogRepository transferLogRepository;

    public TransferLogCreateHelper(TransferLogRepository transferLogRepository) {
        this.transferLogRepository = transferLogRepository;
    }

    public TransferLog persistTransferLog(ExecutionTransferCommand executionTransferCommand) {

        TransferLog transferLog = TransferLog.builder()
                .materialId(new LearningMaterialId(executionTransferCommand.getMaterialId()))
                .createAdminId(executionTransferCommand.getAdminId())
                .build();



        return null;
    }

    private TransferLog saveLog(TransferLog transferLog) {
        TransferLog transferLogResult = transferLogRepository.save(transferLog);
        if (transferLogResult == null) {
            log.error("Could not save order!");
            //throw new OrderDomainException("Could not save order!");
        }

        return transferLogResult;
    }
}
