package com.brunosong.transfer.system.deployment.service.outbox.model;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.deployment.service.valueobject.TransType;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeploymentOutboxMessage {

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
    private OutboxStatus outboxStatus;
    private int version;

}
