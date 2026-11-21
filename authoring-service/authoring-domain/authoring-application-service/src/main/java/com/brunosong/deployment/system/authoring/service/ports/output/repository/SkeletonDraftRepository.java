package com.brunosong.deployment.system.authoring.service.ports.output.repository;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;

import java.util.Optional;

public interface SkeletonDraftRepository {
    void save(SkeletonDraft draft);

    /** 커리큘럼 ID로 뼈대 초안을 찾는다. 없으면 빈 값 */
    Optional<SkeletonDraft> findById(String curriculumId);

    /**
     * 지금 revision 이 expectedRevision 일 때만 고친 초안으로 바꾼다.
     * 바꿨으면 true, 그사이 누가 먼저 고쳤으면(revision 이 다르면) false
     */
    boolean update(SkeletonDraft edited, int expectedRevision);

    /** keyword 가 ID나 제목에 들어 있는 초안을 ID 순으로 한 쪽 읽는다. keyword 가 비면 전부. page 는 0부터 */
    SkeletonDraftPage findPage(String keyword, int page, int size);
}
