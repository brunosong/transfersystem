package com.brunosong.transfer.system.transfer.service.mapper;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransferDataMapper {

    public Transfer executionTransferCommandToTransfer(CreateTransferCommand command) {
        return Transfer.builder()
                .materialId(new LearningMaterialId(command.getMaterialId()))
                .dataMigrationInfoId(new DataMigrationInfoId(command.getDataMigrationInfoId()))
                .build();
    }

    public CreateTransferResponse transferLogToExcutionTransferResponse(Transfer transfer, String message) {
        return CreateTransferResponse.builder()
                .transferLogId(transfer.getId().getValue().toString())
                .message(message)
                .build();
    }

}
