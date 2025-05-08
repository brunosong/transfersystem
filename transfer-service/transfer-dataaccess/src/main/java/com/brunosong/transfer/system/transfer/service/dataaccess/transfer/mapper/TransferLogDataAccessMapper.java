package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferChunkEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.springframework.stereotype.Component;


@Component
public class TransferLogDataAccessMapper {

    public TransferLogEntity transferLogToTransferLogEntity(Transfer transfer) {
        return TransferLogEntity.builder()
                .id(transfer.getId().getValue())
                .sourceId(transfer.getSourceId().getValue())
                .sourceType(transfer.getSourceContentData().getSourceType())
                .sourceconfigId(transfer.getSourceConfigId().getValue())
                .dataMigrationInfoId(transfer.getDataMigrationId().getValue())
                .transType(transfer.getTransType())
                .transferStatus(transfer.getTransferStatus())
                .dbType(transfer.getSourceContentData().getDbType())
                .totalChunkSize(transfer.getSourceContentData().getChunkOffset())
                .createAdminId("BrunoSong")
                .build();
    }

    public Transfer transferLogEntityToTransferLog(TransferLogEntity entity) {
        return Transfer.builder()
                .id(new TransferId(entity.getId()))
                .dataMigrationInfoId(new DataMigrationId(entity.getDataMigrationInfoId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .transType(entity.getTransType())
                .transferStatus(entity.getTransferStatus())
                .totalChunkSize(entity.getTotalChunkSize())
                .build();
    }

    public TransferChunkEntity dataMigrationResponseToChunkEntity(DataMigrationResponse dataMigrationResponse, TransferLogEntity transferLogEntity) {
        return TransferChunkEntity.builder()
                .status(dataMigrationResponse.getTransferStatus().getDescription())
                .chunkOffset(dataMigrationResponse.getChunkOffset())
                .size(dataMigrationResponse.getTotalChunkSize())
                .log(transferLogEntity)
                .build();
    }
}
