package com.brunosong.deployment.system.deployment.service.outbox.model;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeploymentOutboxMessage {

    private UUID id;
    private ZonedDateTime createdAt;

    @Setter
    private ZonedDateTime processedAt;

    private String type;
    private String payload;

    private TransType transType;

    @Setter
    private OutboxStatus outboxStatus;
    private int version;

}
