package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.DeploymentRequestMessagePublisher;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentOutboxRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentOutboxPublisherHelper {

    private final DeploymentRequestMessagePublisher deploymentRequestMessagePublisher;

    public PublishResult publish(DeploymentOutboxMessage outboxMessage) {

        TransType transType = outboxMessage.getTransType();

        if (transType == TransType.MESSAGING) {
            return deploymentRequestMessagePublisher.publish(outboxMessage);
        }

        // API 경로는 아직 아웃박스에서 보낼 수 없다. 아웃박스 행에는 Deployment 가 없고
        // DeploymentApiSendHelper 는 Deployment 를 받는다. 포기로 닫지 않으면
        // 다음 주기가 같은 행을 다시 집어 영원히 돈다
        log.error("Outbox publish is not wired for transType: {} (outbox id: {})",
                transType, outboxMessage.getId());
        return PublishResult.GIVE_UP;
    }

}
