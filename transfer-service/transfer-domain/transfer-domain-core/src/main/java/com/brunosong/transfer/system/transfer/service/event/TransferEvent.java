package com.brunosong.transfer.system.transfer.service.event;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;

import java.time.ZonedDateTime;

public abstract class TransferEvent {

    private final Transfer transfer;
    private final ZonedDateTime createdAt;

    public TransferEvent(Transfer transfer, ZonedDateTime createdAt) {
        this.transfer = transfer;
        this.createdAt = createdAt;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
