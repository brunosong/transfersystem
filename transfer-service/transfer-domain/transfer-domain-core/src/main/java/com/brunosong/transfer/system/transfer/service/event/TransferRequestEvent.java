package com.brunosong.transfer.system.transfer.service.event;

import com.brunosong.transfer.system.transfer.service.valueobject.ChunkContent;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class TransferRequestEvent extends TransferEvent {

    private final int chunkOffset;
    private final ChunkContent content;

    public TransferRequestEvent(Transfer transfer,
                                ZonedDateTime createdAt, int chunkOffset, ChunkContent content) {
        super(transfer, createdAt);
        this.chunkOffset = chunkOffset;
        this.content = content;
    }

}
