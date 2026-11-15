package com.brunosong.deployment.system.deployment.service.ports.output.message.publisher;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.util.function.BiConsumer;

public interface DeploymentRequestMessagePublisher {

    /** 행 하나를 보내고 브로커 응답까지 기다린 뒤 결과를 돌려준다 */
    PublishResult publish(DeploymentOutboxMessage deploymentOutboxMessage);
}
