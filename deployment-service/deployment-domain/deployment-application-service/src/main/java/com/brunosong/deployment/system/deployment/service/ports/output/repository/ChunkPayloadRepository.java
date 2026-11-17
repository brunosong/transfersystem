package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** 청크 내용 보관소. 접수 때 모두 넣고, 보낼 때 번호로 꺼낸다 */
public interface ChunkPayloadRepository {

    /** 목록의 순서가 청크 번호다(0번 뼈대, 1번부터 과목). ChunkContent.split 이 만든 순서 그대로 넘긴다 */
    void saveAll(UUID deploymentId, List<ChunkContent> chunks);

    /** 고른 번호의 청크를 번호 순서로 돌려준다 */
    List<ChunkPayload> findAll(UUID deploymentId, Collection<Integer> chunkNos);

    /**
     * 다시 보낼 일이 없는 배포의 내용을 지우고 지운 행 수를 돌려준다(시스템 설계 6장 "청크 내용은 언제까지 두나").
     * SUCCEEDED 배포, 그리고 같은 커리큘럼 × 대상에 더 최근 배포가 있는 FAILED 배포다.
     * 진행 중인 배포와 가장 최근의 FAILED 배포는 재전송할 수 있어 남긴다
     */
    int deleteNotRetryable();
}
