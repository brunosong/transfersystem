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

    Optional<List<DeploymentOutboxEntity>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
