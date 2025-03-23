package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
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
                .dataMigrationInfoId(transfer.getDataMigrationInfoId().getValue())
                .transType(transfer.getTransType())
                .transferStatus(transfer.getTransferStatus())
                .dbType(transfer.getSourceContentData().getDbType())
                .createAdminId("BrunoSong")
                .build();
    }

    public Transfer transferLogEntityToTransferLog(TransferLogEntity entity) {
        return Transfer.builder()
                .id(new TransferId(entity.getId()))
                //.learningMaterialId(new LearningMaterialId(entity.getSourceId()))
                .dataMigrationInfoId(new DataMigrationId(entity.getDataMigrationInfoId()))
                //.createAdminId(entity.getCreateAdminId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .transType(entity.getTransType())
                .transferStatus(entity.getTransferStatus())
                .build();
    }
}
