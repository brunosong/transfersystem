package com.brunosong.deployment.system.deployment.service.scheduler;

import com.brunosong.deployment.system.deployment.service.handler.DeploymentDeadlineHandler;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 보낸 뒤 기한이 지나도록 결과가 오지 않은 청크를 응답 없음(NO_REPLY)으로 바꾼다.
 * 결과 메시지가 사라져도 배포가 영원히 진행 중으로 남지 않게 한다(도메인 모델 "응답 기한").
 * 기한은 시스템 설계 5장에서 10분으로 정했고, 설정으로 바꿀 수 있다.
 */
@Slf4j
@Component
public class ResponseDeadlineScheduler {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentDeadlineHandler deploymentDeadlineHandler;
    private final Duration deadline;

    public ResponseDeadlineScheduler(DeploymentRepository deploymentRepository,
                                     DeploymentDeadlineHandler deploymentDeadlineHandler,
                                     @Value("${deployment-service.response-deadline.duration:10m}") Duration deadline) {
        this.deploymentRepository = deploymentRepository;
        this.deploymentDeadlineHandler = deploymentDeadlineHandler;
        this.deadline = deadline;
    }

    @Scheduled(fixedDelayString = "${deployment-service.response-deadline.check-interval:60000}")
    public void expireOverdueChunks() {
        Instant now = Instant.now();
        List<UUID> deploymentIds = deploymentRepository.findIdsWithChunksSentBefore(now.minus(deadline));
        if (deploymentIds.isEmpty()) {
            return;
        }
        log.info("응답 기한이 지난 청크가 있는 배포 {}개", deploymentIds.size());

        for (UUID deploymentId : deploymentIds) {
            try {
                deploymentDeadlineHandler.expireOverdue(deploymentId, now, deadline);
            } catch (RuntimeException e) {
                // 같은 배포의 결과 처리와 부딪혔을 수 있다(낙관적 락). 다음 주기에 다시 본다
                log.warn("응답 기한을 처리하지 못했다. 다음 주기에 다시 본다. deploymentId: {}", deploymentId, e);
            }
        }
    }
}
