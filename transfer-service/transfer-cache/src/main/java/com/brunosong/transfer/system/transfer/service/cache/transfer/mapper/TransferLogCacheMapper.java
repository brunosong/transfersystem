package com.brunosong.transfer.system.transfer.service.cache.transfer.mapper;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.cache.transfer.entity.TransferCacheEntity;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransferLogCacheMapper {

    public Transfer toTransfer(TransferCacheEntity transferCacheEntity) {
        return Transfer.builder()
                .id(new TransferId(UUID.fromString(transferCacheEntity.getTransferId())))
                .totalChunkSize(transferCacheEntity.getTotalChunkSize())
                .build();
    }

}
