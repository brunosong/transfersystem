package com.brunosong.transfer.system.deployment.service.ports.output.message.publisher;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.util.function.BiConsumer;

public interface DeploymentRequestMessagePublisher {

    void publish(DeploymentOutboxMessage deploymentOutboxMessage,
                 BiConsumer<DeploymentOutboxMessage, OutboxStatus> outboxCallback);
}
