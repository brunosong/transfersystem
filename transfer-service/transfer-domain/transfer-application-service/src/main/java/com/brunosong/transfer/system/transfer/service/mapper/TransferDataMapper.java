package com.brunosong.transfer.system.transfer.service.mapper;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import org.springframework.stereotype.Component;

@Component
public class TransferDataMapper {

    public Transfer transferRequestToTransfer(TransferRequest request) {
        return Transfer.builder()
                .sourceId(new SourceId(request.getSourceId()))
                .sourceType(SourceType.valueOf(request.getSourceType()))
                .dataMigrationInfoId(new DataMigrationInfoId(request.getDataMigrationInfoId()))
                .transType(request.getTransType())
                .build();
    }

    public CreateTransferResponse transferLogToExecutionTransferResponse(TransferLogResult transferLogResult) {
        return CreateTransferResponse.builder()
                .transferLogId(transferLogResult.transferId())
                .logMessage(transferLogResult.logMessage())
                .build();
    }

}
