package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeploymentDeadlineHandler {

    private final DeploymentRepository deploymentRepository;

    /**
     * 배포 하나의 응답 기한을 확인한다. 배포마다 트랜잭션을 따로 둬서,
     * 같은 배포의 결과 처리와 부딪혀 실패해도 다른 배포는 처리된다.
     * 그사이 배포가 지워졌으면 할 일이 없다.
     */
    @Transactional
    public void expireOverdue(UUID deploymentId, Instant now, Duration deadline) {
        deploymentRepository.findById(deploymentId).ifPresent(deployment -> {
            deployment.expireOverdue(now, deadline);
            deploymentRepository.save(deployment);
        });
    }
}
