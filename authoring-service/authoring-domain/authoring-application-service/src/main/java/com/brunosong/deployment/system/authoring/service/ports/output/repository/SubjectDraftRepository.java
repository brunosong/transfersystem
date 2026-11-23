package com.brunosong.deployment.system.authoring.service.ports.output.repository;


import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;

import java.util.List;
import java.util.Optional;

/** 과목 초안 저장소. 뼈대와 다른 컬렉션(subject_drafts)이다 */
public interface SubjectDraftRepository {

    void save(SubjectDraft draft);

    /** 한 커리큘럼의 과목을 모두 읽는다. 화면에서 학기마다 묶어 보여 준다 */
    List<SubjectDraft> findByCurriculumId(String curriculumId);

    /** 과목 ID로 찾는다. 없으면 빈 값 */
    Optional<SubjectDraft> findById(String subjectId);

    /** 지금 revision 이 expectedRevision 일 때만 바꾼다. 바꿨으면 true, 누가 먼저 고쳤으면 false */
    boolean update(SubjectDraft draft, int expectedRevision);

    /** 지금 revision 이 expectedRevision 일 때만 지운다. 지웠으면 true, 누가 먼저 고쳤으면 false */
    boolean delete(String subjectId, int expectedRevision);
}
