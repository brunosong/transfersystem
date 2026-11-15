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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentOutboxScheduler implements OutboxScheduler {

    private final DeploymentOutboxHelper deploymentOutboxHelper;
    private final DeploymentOutboxPublisherHelper deploymentOutboxPublisherHelper;

    @Override
    @Scheduled(fixedDelayString = "${deployment-service.outbox-scheduler.fixed-delay}",
            initialDelayString = "${deployment-service.outbox-scheduler.initial-delay}")
    public void processOutboxMessages() {
        List<DeploymentOutboxMessage> outboxMessages = deploymentOutboxHelper
                .getDeploymentOutboxMessageByOutboxStatus(OutboxStatus.STARTED)
                .orElse(List.of());
        if (outboxMessages.isEmpty()) {
            return;
        }
        log.info("보낼 아웃박스 행 {}개", outboxMessages.size());

        // 이번 주기에 앞 행을 보내지 못한 키. 같은 키의 뒤 행이 먼저 나가면 순서가 뒤집히므로 건너뛴다
        Set<String> blockedKeys = new HashSet<>();

        for (DeploymentOutboxMessage outboxMessage : outboxMessages) {
            if (blockedKeys.contains(outboxMessage.getMessageKey())) {
                continue;
            }
            switch (deploymentOutboxPublisherHelper.publish(outboxMessage)) {
                case PUBLISHED -> updateOutboxStatus(outboxMessage, OutboxStatus.COMPLETED);
                case GIVE_UP -> updateOutboxStatus(outboxMessage, OutboxStatus.FAILED);
                case RETRY_LATER -> {
                    // 행은 STARTED 그대로 둔다. 다음 주기에 이 행부터 다시 보낸다
                    blockedKeys.add(outboxMessage.getMessageKey());
                    log.warn("다음 주기에 다시 보낸다. 같은 키의 뒤 행도 기다린다. outbox id: {}, key: {}",
                            outboxMessage.getId(), outboxMessage.getMessageKey());
                }
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
