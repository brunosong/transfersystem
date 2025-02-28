package com.brunosong.transfer.system.transfer.service.mapper;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferDataMapper {

    public Transfer createTransferCommandToTransfer(CreateTransferCommand command) {
        return Transfer.builder()
                .materialId(new LearningMaterialId(command.getMaterialId()))
                .dataMigrationInfoId(new DataMigrationInfoId(command.getDataMigrationInfoId()))
                .transType(command.getTransType())
                .build();
    }

    public CreateTransferResponse transferLogToExecutionTransferResponse(TransferLogResult transferLogResult) {
        return CreateTransferResponse.builder()
                .transferLogId(transferLogResult.transferId())
                .logMessage(transferLogResult.logMessage())
                .build();
    }

}
