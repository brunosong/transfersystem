package com.brunosong.deployment.system.authoring.service.ports.input.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftPage;

import java.util.List;

/** 커리큘럼 읽기(입력 포트). 상태를 바꾸지 않는다 */
public interface CurriculumQueryService {

    /** 뼈대 초안 하나. 없으면 CurriculumNotFoundException(404) */
    SkeletonDraft find(String curriculumId);

    /** 목록 한 쪽. keyword 가 비면 전부, page 는 0부터 */
    SkeletonDraftPage findPage(String keyword, int page, int size);

    /** 한 커리큘럼의 과목 초안. 과목은 뼈대와 따로 저장되어 따로 읽는다 */
    List<SubjectDraft> findSubjects(String curriculumId);
}
