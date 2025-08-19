package com.brunosong.transfer.system.transfer.service.mapper;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class TransferDataMapper {

    public Transfer transferRequestToTransfer(TransferRequest request) {
        return Transfer.builder()
                .sourceId(new SourceId(request.getSourceId()))
                .sourceConfigId(new SourceConfigId(request.getSourceConfigId()))
                .dataMigrationInfoId(new DataMigrationId(request.getDataMigrationInfoId()))
                .transType(request.getTransType())
                .build();
    }

    public CreateTransferResponse transferLogToExecutionTransferResponse(TransferLogResult transferLogResult) {
        return CreateTransferResponse.builder()
                .transferLogId(transferLogResult.transferId())
                .logMessage(transferLogResult.logMessage())
                .build();
    }

    public SourceConfigId toSourceConfigId(TransferRequest request) {
        return new SourceConfigId(request.getSourceConfigId());
    }

    public Transfer dataMigrationResponseToTransfer(DataMigrationResponse dataMigrationResponse) {
        return Transfer.builder()
                .id(dataMigrationResponse.getTransferId())
                .transferStatus(dataMigrationResponse.getTransferStatus())
                .resultMessage(dataMigrationResponse.getMessage())
                .sourceContentData(SourceContentData.builder().chunkOffset(dataMigrationResponse.getChunkOffset()).build())
                .build();
    }

    public DataTransferEventPayload transferRequestEventToDataTransferEventPayload(TransferRequestEvent transferRequestEvent, byte[] data, int chunkOffset) {
        return DataTransferEventPayload.builder()
                .transferId(transferRequestEvent.getTransferId())
                .dataMigrationId(transferRequestEvent.getTransfer().getDataMigrationId())
                .data(data)
                .chunkOffset(chunkOffset)
                .createdAt(transferRequestEvent.getCreatedAt())
                .build();
    }
}
