package com.brunosong.transfer.system.transfer.service.event;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;

import java.time.ZonedDateTime;

public abstract class TransferEvent {
    private final TransType transType;
    private final SourceContentData sourceContentData;
    private final ZonedDateTime createdAt;

    public TransferEvent(TransType transType, SourceContentData sourceContentData, ZonedDateTime createdAt) {
        this.transType = transType;
        this.sourceContentData = sourceContentData;
        this.createdAt = createdAt;
    }

    public TransType getTransType() {
        return transType;
    }

    public SourceContentData getSourceContentData() {
        return sourceContentData;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }


}
