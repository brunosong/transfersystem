package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeploymentOutboxJpaRepository extends JpaRepository<DeploymentOutboxEntity, UUID> {

    /** 보낼 행을 넣은 순서(seq)대로, 한 번에 100개까지 읽는다 */
    Optional<List<DeploymentOutboxEntity>> findTop100ByTypeAndOutboxStatusOrderBySeqAsc(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    /** 고른 상태의 행 중 처리 시각이 before 보다 이른 것을 한 문장으로 지운다. 엔티티를 읽어 오지 않는다 */
    @Transactional
    @Modifying
    @Query("delete from DeploymentOutboxEntity o where o.outboxStatus in :statuses and o.processedAt < :before")
    int deleteByOutboxStatusInAndProcessedAtBefore(@Param("statuses") Collection<OutboxStatus> statuses,
                                                   @Param("before") ZonedDateTime before);
}
