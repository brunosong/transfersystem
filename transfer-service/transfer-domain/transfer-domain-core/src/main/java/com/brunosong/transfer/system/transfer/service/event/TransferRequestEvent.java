package com.brunosong.transfer.system.transfer.service.event;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class TransferRequestEvent extends TransferEvent {

    private final int chunkOffset;
    private final byte[] chunkData;

    public TransferRequestEvent(Transfer transfer,
                                ZonedDateTime createdAt, int chunkOffset, byte[] chunkData) {
        super(transfer, createdAt);
        this.chunkOffset = chunkOffset;
        this.chunkData = chunkData;
    }

}
