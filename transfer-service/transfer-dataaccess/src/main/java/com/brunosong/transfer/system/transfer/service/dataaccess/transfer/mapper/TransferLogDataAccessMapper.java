package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferLogDataAccessMapper {

    public TransferLogEntity transferLogToTransferLogEntity(Transfer transfer) {
        return TransferLogEntity.builder()
                .id(transfer.getId().getValue())
                .createAdminId(transfer.getCreateAdminId())
                .build();
    }

    public Transfer transferLogEntityToTransferLog(TransferLogEntity transferLogEntity) {
        return Transfer.builder()
                .id(new TransferId(transferLogEntity.getId()))
                .createAdminId(transferLogEntity.getCreateAdminId())
                .createdAt(transferLogEntity.getCreatedAt())
                .updatedAt(transferLogEntity.getUpdatedAt())
                .build();
    }
}
