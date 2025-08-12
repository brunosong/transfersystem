package com.brunosong.transfer.system.transfer.service.event;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class TransferRequestEvent extends TransferEvent {

    @Setter
    private TransferId transferId;

    public TransferRequestEvent(Transfer transfer,
                                SourceContentData sourceContentData,
                                ZonedDateTime createdAt) {
        super(transfer, sourceContentData, createdAt);
    }

}
