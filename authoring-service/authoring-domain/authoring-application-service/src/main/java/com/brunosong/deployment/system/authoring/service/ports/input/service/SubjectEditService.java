package com.brunosong.deployment.system.authoring.service.ports.input.service;

import com.brunosong.deployment.system.authoring.service.entity.Lesson;
import com.brunosong.deployment.system.authoring.service.entity.MoveDirection;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.entity.Unit;

/**
 * 과목 초안 고치기(입력 포트). 과목마다 자기 revision 으로 낙관적 락을 건다.
 * expectedRevision 은 화면이 과목을 열 때 받아 둔 그 과목의 revision 이다. 다르면 RevisionConflictException(409)
 */
public interface SubjectEditService {

    /** 단원을 하나 붙인다. 과목이 없으면 SubjectNotFoundException(404). 새 단원과 과목의 새 revision 을 돌려준다 */
    AddedUnit addUnit(String curriculumId, String subjectId, String title, String description, int expectedRevision);

    /** 과목 제목, 설명, 정렬 순서를 고친다. 과목이 없으면 SubjectNotFoundException(404), 누가 먼저 고쳤으면 409 */
    SubjectDraft editSubject(String curriculumId, String subjectId, String title, String description, int sortOrder,
                             int expectedRevision);

    /** 단원을 한 칸 옮긴다. 맨 끝이라 못 옮기면 그대로 둔다. 과목이나 단원이 없으면 404 */
    SubjectDraft moveUnit(String curriculumId, String subjectId, String unitId, MoveDirection direction,
                          int expectedRevision);

    /** 단원 안에서 차시를 한 칸 옮긴다. 과목·단원·차시가 없으면 404 */
    SubjectDraft moveLesson(String curriculumId, String subjectId, String unitId, String lessonId,
                            MoveDirection direction, int expectedRevision);

    /** 단원에 차시를 붙인다. 과목이 없으면 SubjectNotFoundException, 단원이 없으면 UnitNotFoundException(둘 다 404) */
    AddedLesson addLesson(String curriculumId, String subjectId, String unitId, String title, String description,
                          int expectedRevision);

    /** 단원의 제목과 설명을 고친다. 과목이 없으면 SubjectNotFoundException, 단원이 없으면 UnitNotFoundException(둘 다 404) */
    SubjectDraft editUnit(String curriculumId, String subjectId, String unitId, String title, String description,
                          int expectedRevision);

    /** 차시의 제목, 설명, 학습 수준(1~5)을 고친다. 과목·단원·차시가 없으면 각각의 NotFoundException(404) */
    SubjectDraft editLesson(String curriculumId, String subjectId, String unitId, String lessonId, String title,
                            String description, int learningLevel, int expectedRevision);

    /** 과목을 지운다. 단원과 차시도 함께 사라진다. 과목이 없으면 404, 누가 먼저 고쳤으면 409 */
    void removeSubject(String curriculumId, String subjectId, int expectedRevision);

    /** 단원을 지운다. 그 안의 차시도 함께 사라진다. 과목이나 단원이 없으면 404 */
    SubjectDraft removeUnit(String curriculumId, String subjectId, String unitId, int expectedRevision);

    /** 차시를 지운다. 과목·단원·차시가 없으면 404 */
    SubjectDraft removeLesson(String curriculumId, String subjectId, String unitId, String lessonId,
                              int expectedRevision);

    /** 단원 추가의 결과. 새 단원과 저장된 뒤의 과목 revision */
    record AddedUnit(Unit unit, int revision) {
    }

    /** 차시 추가의 결과. 새 차시와 저장된 뒤의 과목 revision */
    record AddedLesson(Lesson lesson, int revision) {
    }
}
