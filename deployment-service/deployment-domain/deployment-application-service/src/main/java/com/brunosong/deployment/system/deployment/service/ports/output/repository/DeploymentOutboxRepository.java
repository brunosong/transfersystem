package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface DeploymentOutboxRepository {

    DeploymentOutboxMessage save(DeploymentOutboxMessage deploymentOutboxMessage);

    /** 넣은 순서(seq)대로, 한 번에 100개까지 */
    Optional<List<DeploymentOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    /**
     * 처리가 끝난(COMPLETED, FAILED) 행 중 처리 시각이 before 보다 이른 것을 지우고 지운 수를 돌려준다.
     * 아직 보내지 않은 STARTED 행은 지우지 않는다
     */
    int deleteProcessedBefore(ZonedDateTime before);
}
