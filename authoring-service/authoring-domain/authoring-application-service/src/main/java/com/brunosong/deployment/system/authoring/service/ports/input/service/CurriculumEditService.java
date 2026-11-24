package com.brunosong.deployment.system.authoring.service.ports.input.service;

import com.brunosong.deployment.system.authoring.service.entity.Grade;
import com.brunosong.deployment.system.authoring.service.entity.MoveDirection;
import com.brunosong.deployment.system.authoring.service.entity.Semester;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;

/**
 * 뼈대 초안 고치기(입력 포트). 모두 낙관적 락을 쓴다.
 * expectedRevision 은 화면이 편집을 열 때 받아 둔 revision 이다. 다르면 RevisionConflictException(409)
 */
public interface CurriculumEditService {

    /** 커리큘럼 제목, 설명, 정렬 순서를 고친다 */
    SkeletonDraft edit(String curriculumId, String title, String description, int sortOrder, int expectedRevision);

    /** 학년을 한 칸 옮긴다. 맨 끝이라 못 옮기면 그대로 둔다. 학년이 없으면 GradeNotFoundException(404) */
    SkeletonDraft moveGrade(String curriculumId, String gradeId, MoveDirection direction, int expectedRevision);

    /** 학년 안에서 학기를 한 칸 옮긴다. 학년이나 학기가 없으면 각각의 NotFoundException(404) */
    SkeletonDraft moveSemester(String curriculumId, String gradeId, String semesterId, MoveDirection direction,
                               int expectedRevision);

    /** 학년을 하나 붙인다. 새 학년의 ID와 새 revision 을 돌려준다 */
    AddedGrade addGrade(String curriculumId, String title, String description, int expectedRevision);

    /** 학년의 제목과 설명을 고친다. 학년이 없으면 GradeNotFoundException(404) */
    SkeletonDraft editGrade(String curriculumId, String gradeId, String title, String description, int expectedRevision);

    /**
     * 학년을 지운다. 그 안의 학기도 함께 사라진다. 학년이 없으면 GradeNotFoundException(404).
     * 그 학년의 학기에 과목이 남아 있으면 SemesterHasSubjectsException(422)
     */
    SkeletonDraft removeGrade(String curriculumId, String gradeId, int expectedRevision);

    /**
     * 학기를 지운다. 학년이나 학기가 없으면 각각의 NotFoundException(404).
     * 그 학기에 과목이 남아 있으면 SemesterHasSubjectsException(422)
     */
    SkeletonDraft removeSemester(String curriculumId, String gradeId, String semesterId, int expectedRevision);

    /** 학년에 학기를 붙인다. 학년이 없으면 GradeNotFoundException(404) */
    AddedSemester addSemester(String curriculumId, String gradeId, String title, String description, int expectedRevision);

    /** 학기의 제목과 설명을 고친다. 학년이나 학기가 없으면 각각의 NotFoundException(404) */
    SkeletonDraft editSemester(String curriculumId, String gradeId, String semesterId, String title, String description,
                               int expectedRevision);

    /** 학년 추가의 결과. 새 학년과 저장된 뒤의 revision */
    record AddedGrade(Grade grade, int revision) {
    }

    /** 학기 추가의 결과. 새 학기와 저장된 뒤의 revision */
    record AddedSemester(Semester semester, int revision) {
    }
}
