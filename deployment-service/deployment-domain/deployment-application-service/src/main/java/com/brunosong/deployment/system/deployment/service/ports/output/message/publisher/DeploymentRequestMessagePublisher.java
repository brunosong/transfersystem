package com.brunosong.deployment.system.deployment.service.ports.output.message.publisher;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.util.function.BiConsumer;

public interface DeploymentRequestMessagePublisher {

    void publish(DeploymentOutboxMessage deploymentOutboxMessage,
                 BiConsumer<DeploymentOutboxMessage, OutboxStatus> outboxCallback);
}
