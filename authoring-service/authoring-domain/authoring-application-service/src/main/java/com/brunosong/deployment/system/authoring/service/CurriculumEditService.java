package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 커리큘럼 제목과 설명 고치기. 낙관적 락으로 남의 수정을 덮어쓰지 않는다 */
@Service
@RequiredArgsConstructor
public class CurriculumEditService {

    private final SkeletonDraftRepository skeletonDraftRepository;

    public SkeletonDraft edit(String curriculumId, String title, String description, int expectedRevision) {
        // 1. 지금 초안을 읽는다. 없으면 404
        SkeletonDraft current = skeletonDraftRepository
                .findById(curriculumId).orElseThrow(() -> new CurriculumNotFoundException(curriculumId));

        // 2. 도메인이 고친다. 제목 검사와 revision + 1은 도메인이 한다.
        SkeletonDraft edited = current.edit(title, description);

        // 3. 내가 본 revision 일 때만 저장한다. 아니면 409
        // 1번에서 읽은 revision 이 아니라 화면이 보낸 revision 으로 확인한다.
        // 화면을 연 뒤로 누가 고쳤는지가 궁금한 것이기 때문이다.
        if (!skeletonDraftRepository.update(edited, expectedRevision)) {
            throw new RevisionConflictException(curriculumId, expectedRevision);
        }
        return edited;
    }
}
