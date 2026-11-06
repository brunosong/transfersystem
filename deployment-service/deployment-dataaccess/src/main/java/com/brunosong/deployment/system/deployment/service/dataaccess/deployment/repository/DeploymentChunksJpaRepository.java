package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeploymentChunksJpaRepository extends JpaRepository<DeploymentChunkEntity, Long> {

    boolean existsByLog_IdAndChunkOffset(UUID deploymentId, int chunkOffset);

    List<DeploymentChunkEntity> findAllByLog_IdOrderByChunkOffsetAsc(UUID deploymentId);

    /**
     * 배포별·상태별 청크 수. 목록 화면의 진행률을 한 번의 쿼리로 구한다.
     * 상태는 "Success" 처럼 설명 문자열로 저장돼 있어 대문자로 맞춘다.
     */
    @Query("select c.log.id as deploymentId, upper(c.status) as status, count(c) as total "
            + "from DeploymentChunkEntity c where c.log.id in :ids group by c.log.id, upper(c.status)")
    List<ChunkStatusCount> countByStatus(@Param("ids") Collection<UUID> deploymentIds);

    interface ChunkStatusCount {
        UUID getDeploymentId();

        String getStatus();

        long getTotal();
    }
}
