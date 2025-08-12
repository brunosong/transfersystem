package com.brunosong.transfer.system.transfer.service.event;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;

import java.time.ZonedDateTime;

public abstract class TransferEvent {
    private final Transfer transfer;
    private final SourceContentData sourceContentData;
    private final ZonedDateTime createdAt;

    public TransferEvent(Transfer transfer, SourceContentData sourceContentData, ZonedDateTime createdAt) {
        this.transfer = transfer;
        this.sourceContentData = sourceContentData;
        this.createdAt = createdAt;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public SourceContentData getSourceContentData() {
        return sourceContentData;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }


}
