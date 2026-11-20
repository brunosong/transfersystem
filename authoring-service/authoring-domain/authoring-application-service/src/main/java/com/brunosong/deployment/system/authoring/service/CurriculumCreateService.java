package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.CurriculumNumberIssuer;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 커리큘럼 만들기 . 번호를 받아 빈 뼈대 초안을 만든다. */
@Service
@RequiredArgsConstructor
public class CurriculumCreateService {

    private final CurriculumNumberIssuer curriculumNumberIssuer;
    private final SkeletonDraftRepository skeletonDraftRepository;

    public SkeletonDraft create(String title, String description) {
        // 1. 번호표에서 번호를 받아 사람이 읽기 쉬운 ID로 만든다. (7 -> CUR007)
        String curriculumId = String.format("CUR%03d", curriculumNumberIssuer.next());

        // 2. 도메인이 초안을 만든다. 제목 검사는 도메인이 한다.
        SkeletonDraft draft = SkeletonDraft.create(curriculumId, title, description);

        // 3. 저장한다.
        skeletonDraftRepository.save(draft);
        return draft;
    }
}
