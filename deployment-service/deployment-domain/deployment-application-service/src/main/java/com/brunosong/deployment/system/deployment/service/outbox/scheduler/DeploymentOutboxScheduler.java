package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.domain.DomainConstants;
import com.brunosong.deployment.system.outbox.OutboxScheduler;
import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.DeploymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentOutboxScheduler implements OutboxScheduler {

    private final DeploymentOutboxHelper deploymentOutboxHelper;
    private final DeploymentOutboxPublisherHelper deploymentOutboxPublisherHelper;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${deployment-service.outbox-scheduler.fixed-delay}",
            initialDelayString = "${deployment-service.outbox-scheduler.initial-delay}")
    public void processOutboxMessages() {
        Optional<List<DeploymentOutboxMessage>> outboxMessagesResponse = deploymentOutboxHelper.getDeploymentOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<DeploymentOutboxMessage> outboxMessages = outboxMessagesResponse.get();

            log.info("Received {} DeploymentOutboxMessages with ids : {}, sending to message bus!", outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage ->
                            outboxMessage.getId().toString()).collect(Collectors.joining()));

            for (DeploymentOutboxMessage outboxMessage : outboxMessages) {
                deploymentOutboxPublisherHelper.publish(outboxMessage, this::updateOutboxStatus);
            }
        }
    }

    private void updateOutboxStatus(DeploymentOutboxMessage deploymentOutboxMessage, OutboxStatus outboxStatus) {
        deploymentOutboxMessage.setOutboxStatus(outboxStatus);
        // 언제 나갔는지가 없으면 밀린 건과 방금 나간 건을 구별할 수 없다
        deploymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
        deploymentOutboxHelper.save(deploymentOutboxMessage);
        log.info("Updated DeploymentOutboxMessage with ID: {} to status: {}", deploymentOutboxMessage.getId(), outboxStatus);

    }
}
