package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeploymentJpaRepository extends JpaRepository<DeploymentEntity, UUID> {

    Page<DeploymentEntity> findAllByDeploymentStatus(DeploymentStatus deploymentStatus, Pageable pageable);

    @Query("select t.deploymentStatus as status, count(t) as total from DeploymentEntity t group by t.deploymentStatus")
    List<StatusCount> countByStatus();

    interface StatusCount {
        DeploymentStatus getStatus();

        long getTotal();
    }
}
