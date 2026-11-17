package com.brunosong.deployment.system.deployment.service.scheduler;

import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentOutboxRepository;
import com.brunosong.deployment.system.domain.DomainConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 쓸모가 끝난 행을 주기적으로 지운다. 표가 계속 커지지 않게 한다.
 * - 아웃박스: 처리가 끝난 지 보관 기간(기본 7일, 토픽 보관과 같다)이 지난 행
 * - 청크 내용: 다시 보낼 일이 없는 배포의 내용(시스템 설계 6장 "청크 내용은 언제까지 두나")
 * 결과 처리 트랜잭션을 무겁게 하지 않으려고 따로 돈다.
 * 여러 대가 함께 돌아도 지우는 결과가 같아서 락을 걸지 않는다.
 */
@Slf4j
@Component
public class CleanupScheduler {

    private final DeploymentOutboxRepository deploymentOutboxRepository;
    private final ChunkPayloadRepository chunkPayloadRepository;
    private final Duration outboxRetention;

    public CleanupScheduler(DeploymentOutboxRepository deploymentOutboxRepository,
                            ChunkPayloadRepository chunkPayloadRepository,
                            @Value("${deployment-service.cleanup.outbox-retention:7d}") Duration outboxRetention) {
        this.deploymentOutboxRepository = deploymentOutboxRepository;
        this.chunkPayloadRepository = chunkPayloadRepository;
        this.outboxRetention = outboxRetention;
    }

    @Scheduled(fixedDelayString = "${deployment-service.cleanup.interval:3600000}")
    public void cleanUp() {
        // 둘은 서로 상관없다. 하나가 실패해도 다른 하나는 지운다. 실패한 것은 다음 주기에 다시 지운다
        try {
            ZonedDateTime before = ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)).minus(outboxRetention);
            int deleted = deploymentOutboxRepository.deleteProcessedBefore(before);
            log.info("처리가 끝난 지 {} 지난 아웃박스 행 {}개를 지웠다", outboxRetention, deleted);
        } catch (RuntimeException e) {
            log.warn("아웃박스 행을 지우지 못했다. 다음 주기에 다시 지운다", e);
        }

        try {
            int deleted = chunkPayloadRepository.deleteNotRetryable();
            log.info("다시 보낼 일이 없는 청크 내용 {}개를 지웠다", deleted);
        } catch (RuntimeException e) {
            log.warn("청크 내용을 지우지 못했다. 다음 주기에 다시 지운다", e);
        }
    }
}
