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
}
