package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChunkJpaRepository extends JpaRepository<ChunkEntity, ChunkEntityId> {

    /** 배포 하나의 청크를 번호 순서로 읽는다. 배포를 되살릴 때 쓴다. */
    List<ChunkEntity> findAllById_DeploymentIdOrderById_ChunkNoAsc(UUID deploymentId);
}
