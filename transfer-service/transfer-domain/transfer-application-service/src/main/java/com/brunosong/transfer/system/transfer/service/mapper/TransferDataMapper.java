package com.brunosong.transfer.system.transfer.service.mapper;

import com.brunosong.transfer.system.transfer.service.helper.transfer.TargetCatalog;
import com.brunosong.transfer.system.transfer.service.valueobject.ChunkContent;
import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import org.springframework.stereotype.Component;

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

    public DataTransferEventPayload transferRequestEventToDataTransferEventPayload(TransferRequestEvent transferRequestEvent) {
        TargetCatalog.Target target = TargetCatalog.find(transferRequestEvent.getTransfer().getDataMigrationId().getValue());
        ChunkContent content = transferRequestEvent.getContent();
        return DataTransferEventPayload.builder()
                .transferId(transferRequestEvent.getTransfer().getId().getValue())
                .targetSystem(target.system())
                .environment(target.environment())
                .transType(transferRequestEvent.getTransfer().getTransType())
                .sourceId(transferRequestEvent.getTransfer().getSourceId().getValue())
                .skeleton(content instanceof ChunkContent.Skeleton skeleton ? skeleton : null)
                .subjectChunk(content instanceof ChunkContent.SubjectChunk chunk ? chunk : null)
                .chunkOffset(transferRequestEvent.getChunkOffset())
                .createdAt(transferRequestEvent.getCreatedAt())
                .build();
    }
}
