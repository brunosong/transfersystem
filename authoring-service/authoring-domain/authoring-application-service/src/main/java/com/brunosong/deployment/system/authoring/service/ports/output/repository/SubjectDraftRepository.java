package com.brunosong.deployment.system.authoring.service.ports.output.repository;


import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;

import java.util.List;

/** 과목 초안 저장소. 뼈대와 다른 컬렉션(subject_drafts)이다 */
public interface SubjectDraftRepository {

    void save(SubjectDraft draft);

    /** 한 커리큘럼의 과목을 모두 읽는다. 화면에서 학기마다 묶어 보여 준다 */
    List<SubjectDraft> findByCurriculumId(String curriculumId);
}
