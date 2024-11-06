package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferExecutionHandler {

    private final LearningMaterialCreateHelper learningMaterialCreateHelper;
    private final TransferSendServiceHelper transferSendServiceHelper;
    private final TransferLogCreateHelper transferLogCreateHelper;

    private final TransferDataMapper transferDataMapper;

    @Transactional
    public ExcutionTransferResponse execution(ExecutionTransferCommand executionTransferCommand) {

        LearningMaterial material =
                learningMaterialCreateHelper.createMaterial(executionTransferCommand);

        if (executionTransferCommand.getTransType() == TransType.API) {
            transferSendServiceHelper.transferAction(material);
        }

        TransferLog transferLog = transferLogCreateHelper.persistTransferLog(executionTransferCommand);

        ExcutionTransferResponse excutionTransferResponse =
                transferDataMapper.transferLogToExcutionTransferResponse(transferLog, "Create transfer log success");

        return excutionTransferResponse;
    }

}
