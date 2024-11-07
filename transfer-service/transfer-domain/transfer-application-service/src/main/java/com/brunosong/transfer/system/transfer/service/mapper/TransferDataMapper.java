package com.brunosong.transfer.system.transfer.service.mapper;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import org.springframework.stereotype.Component;

@Component
public class TransferDataMapper {

    public TransferLog executionTransferCommandToTransferLog(ExecutionTransferCommand executionTransferCommand) {
        return TransferLog.builder()
                .materialId(new LearningMaterialId(executionTransferCommand.getMaterialId()))
                .createAdminId(executionTransferCommand.getAdminId())
                .build();
    }

    public ExcutionTransferResponse transferLogToExcutionTransferResponse(TransferLog transferLog, String message) {
        return ExcutionTransferResponse.builder()
                .transferLogId(transferLog.getId().getValue().toString())
                .message(message)
                .build();
    }

}
