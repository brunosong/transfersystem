package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 커리큘럼 읽기. 편집 화면을 열 때 내용과 revision 을 함께 준다 */
@Service
@RequiredArgsConstructor
public class CurriculumQueryService {

    private final SkeletonDraftRepository skeletonDraftRepository;

    public SkeletonDraft find(String curriculumId) {
        // 없으면 예외로 바꾼다. 컨트롤러 쪽에서 404 로 돌려준다
        return skeletonDraftRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));
    }

}
