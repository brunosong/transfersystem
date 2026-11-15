package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeploymentOutboxJpaRepository extends JpaRepository<DeploymentOutboxEntity, UUID> {

    /** 보낼 행을 넣은 순서(seq)대로, 한 번에 100개까지 읽는다 */
    Optional<List<DeploymentOutboxEntity>> findTop100ByTypeAndOutboxStatusOrderBySeqAsc(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
