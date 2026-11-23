package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftPage;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SubjectDraftRepository;
import lombok.RequiredArgsConstructor;
import com.brunosong.deployment.system.authoring.service.ports.input.service.CurriculumQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

/** 커리큘럼 읽기. 편집 화면을 열 때 내용과 revision 을 함께 준다 */
@Service
@RequiredArgsConstructor
public class CurriculumQueryServiceImpl implements CurriculumQueryService {

    private final SkeletonDraftRepository skeletonDraftRepository;
    private final SubjectDraftRepository subjectDraftRepository;

    @Override
    public SkeletonDraft find(String curriculumId) {
        // 없으면 예외로 바꾼다. 컨트롤러 쪽에서 404 로 돌려준다
        return skeletonDraftRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));
    }

    /** 목록 한 쪽. keyword 가 비면 전부, page 는 0부터 */
    @Override
    public SkeletonDraftPage findPage(String keyword, int page, int size) {
        return skeletonDraftRepository.findPage(keyword, page, size);
    }

    /** 한 커리큘럼의 과목 초안. 화면이 학기마다 묶어 보여 준다. 과목은 뼈대와 따로 저장되어 따로 읽는다 */
    @Override
    public List<SubjectDraft> findSubjects(String curriculumId) {
        return subjectDraftRepository.findByCurriculumId(curriculumId);
    }
}
