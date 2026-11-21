package com.brunosong.deployment.system.authoring.service.ports.output.repository;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;

import java.util.Optional;

public interface SkeletonDraftRepository {
    void save(SkeletonDraft draft);

    /** 커리큘럼 ID로 뼈대 초안을 찾는다. 없으면 빈 값 */
    Optional<SkeletonDraft> findById(String curriculumId);
}
