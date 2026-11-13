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
}
