package com.brunosong.deployment.system.deployment.service.ports.output.message.publisher;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;

import java.util.function.BiConsumer;

public interface DeploymentRequestMessagePublisher {

    /**
     * 행 하나를 보내고 브로커 응답까지 기다린 뒤 결과를 돌려준다.
     * 페이로드는 행에서 읽은 것, 내용은 내용 표에서 읽은 것을 받는다. 어댑터는 DB 를 모른다
     */
    PublishResult publish(DeploymentOutboxMessage deploymentOutboxMessage, DeploymentEventPayload payload, ChunkContent content);
}
