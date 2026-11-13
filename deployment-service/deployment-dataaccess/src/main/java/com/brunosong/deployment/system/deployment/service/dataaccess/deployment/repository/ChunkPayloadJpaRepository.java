package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;


import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkPayloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ChunkPayloadJpaRepository extends JpaRepository<ChunkPayloadEntity, ChunkEntityId> {

    List<ChunkPayloadEntity> findAllById_DeploymentIdAndId_ChunkNoInOrderById_ChunkNoAsc(UUID deploymentId, Collection<Integer> chunkNos);
}
