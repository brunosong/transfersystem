package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper;

import com.brunosong.transfer.system.domain.valueobject.TransferLogId;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import org.springframework.stereotype.Component;

@Component
public class TransferLogDataAccessMapper {

    public TransferLogEntity transferLogToTransferLogEntity(TransferLog transferLog) {
        return TransferLogEntity.builder()
                .id(transferLog.getId().getValue())
                .createAdminId(transferLog.getCreateAdminId())
                .build();
    }

    public TransferLog transferLogEntityToTransferLog(TransferLogEntity transferLogEntity) {
        return TransferLog.builder()
                .id(new TransferLogId(transferLogEntity.getId()))
                .createAdminId(transferLogEntity.getCreateAdminId())
                .createdAt(transferLogEntity.getCreatedAt())
                .updatedAt(transferLogEntity.getUpdatedAt())
                .build();
    }
}
