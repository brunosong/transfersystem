package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;


import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkPayloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ChunkPayloadJpaRepository extends JpaRepository<ChunkPayloadEntity, ChunkEntityId> {

    List<ChunkPayloadEntity> findAllById_DeploymentIdAndId_ChunkNoInOrderById_ChunkNoAsc(UUID deploymentId, Collection<Integer> chunkNos);

    /**
     * 다시 보낼 일이 없는 배포의 내용을 지운다.
     * 더 최근 배포는 같은 커리큘럼 × 대상에서 만든 시각이 더 늦은 배포다(규칙 B: 재전송은 가장 최근 배포만).
     * 커리큘럼 ID가 없는 옛 배포(V10 전)는 = 비교가 참이 되지 않아 이 조건에서 빠진다
     */
    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM deployment_chunk_payload p
             USING deployment d
             WHERE p.deployment_id = d.id
               AND (d.deployment_status = 'SUCCEEDED'
                    OR (d.deployment_status = 'FAILED'
                        AND EXISTS (SELECT 1
                                      FROM deployment newer
                                     WHERE newer.curriculum_id = d.curriculum_id
                                       AND newer.target_id = d.target_id
                                       AND newer.created_at > d.created_at)))
            """, nativeQuery = true)
    int deleteNotRetryable();
}
