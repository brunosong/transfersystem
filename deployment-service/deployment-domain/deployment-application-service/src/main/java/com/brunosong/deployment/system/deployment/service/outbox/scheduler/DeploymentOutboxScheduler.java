package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.deployment.service.ports.output.lock.PublishLock;
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
    private final PublishLock publishLock;

    /**
     * 기본은 1초마다 돈다. 뼈대 결과가 온 뒤 과목이 나가기까지의 지연이 이 주기만큼 늘기 때문이다.
     * 보낼 행이 없으면 seq 순서 조회 한 번으로 끝나서 짧게 돌려도 부담이 적다.
     * 로컬은 흐름을 천천히 보려고 application-local.yml 에서 늘려 둔다. 첫 실행은 앱이 다 뜬 뒤로 미룬다(기본 10초).
     */
    @Override
    @Scheduled(fixedDelayString = "${deployment-service.outbox-scheduler.fixed-delay:1000}",
            initialDelayString = "${deployment-service.outbox-scheduler.initial-delay:10000}")
    public void processOutboxMessages() {
        // 여러 대를 띄워도 한 대만 발행한다. 락을 못 잡으면 이번 주기는 건너뛰고 다음 주기에 다시 시도한다
        if (!publishLock.runIfFree(this::publishStartedRows)) {
            log.debug("다른 인스턴스가 발행 중이라 이번 주기는 건너뛴다");
        }
    }

    /** STARTED 행을 seq 순서로 보낸다. 락을 쥔 인스턴스에서만 돈다 */
    private void publishStartedRows() {
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
