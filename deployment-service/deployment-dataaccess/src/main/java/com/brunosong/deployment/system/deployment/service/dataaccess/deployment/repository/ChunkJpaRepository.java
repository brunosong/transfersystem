package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChunkJpaRepository extends JpaRepository<ChunkEntity, ChunkEntityId> {

    /** 배포 하나의 청크를 번호 순서로 읽는다. 배포를 되살릴 때 쓴다. */
    List<ChunkEntity> findAllById_DeploymentIdOrderById_ChunkNoAsc(UUID deploymentId);

    /**
     * 이 시각 전에 보낸 뒤 아직 응답을 기다리는(SENT) 청크가 있는 배포의 ID. 응답 기한 스케줄러가 쓴다.
     * 상태를 매개변수가 아니라 글자 그대로(SENT) 적는다. 그래야 V5 에 만든 부분 인덱스
     * (deployment_chunk (sent_at) WHERE status = 'SENT')를 DB가 쓸 수 있다.
     */
    @Query("select distinct c.id.deploymentId from ChunkEntity c "
            + "where c.status = com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus.SENT "
            + "and c.sentAt < :sentBefore")
    List<UUID> findDeploymentIdsWaitingForReplySentBefore(@Param("sentBefore") Instant sentBefore);
}
