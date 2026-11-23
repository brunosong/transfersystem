package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.SemesterNotFoundException;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SubjectDraftRepository;
import lombok.RequiredArgsConstructor;
import com.brunosong.deployment.system.authoring.service.ports.input.service.SubjectCreateService;
import org.springframework.stereotype.Service;

/**
 * 과목 만들기. 과목 초안은 뼈대와 다른 애그리거트라, 뼈대를 고치지 않고 따로 저장한다.
 * "과목은 뼈대에 있는 학기에만 속한다"(불변식 6)는 두 애그리거트에 걸친 규칙이라 여기서 지킨다
 */
@Service
@RequiredArgsConstructor
public class SubjectCreateServiceImpl implements SubjectCreateService {

    private final SkeletonDraftRepository skeletonDraftRepository;
    private final SubjectDraftRepository subjectDraftRepository;

    @Override
    public SubjectDraft create(String curriculumId, String semesterId, String title, String description) {
        // 1. 뼈대를 읽는다. 커리큘럼이 없으면 404
        SkeletonDraft skeleton = skeletonDraftRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));

        // 2. 그 학기가 뼈대에 있는지 본다. 없으면 404
        if (!skeleton.hasSemester(semesterId)) {
            throw new SemesterNotFoundException(semesterId);
        }

        // 3. 과목을 만들어 따로 저장한다. 뼈대는 고치지 않으므로 뼈대의 revision 은 그대로다
        SubjectDraft subject = SubjectDraft.create(curriculumId, semesterId, title, description);
        subjectDraftRepository.save(subject);
        return subject;
    }
}
