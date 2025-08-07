package com.brunosong.transfer.system.transfer.service.outbox.model;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DataTransferOutboxMessage {

    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;

    @Setter
    private ZonedDateTime processedAt;

    private String type;
    private String payload;

    @Setter
    private SagaStatus sagaStatus;
    private TransType transType;

    @Setter
    private TransferStatus transferStatus;

    @Setter
    private OutboxStatus outboxStatus;
    private int version;

}
