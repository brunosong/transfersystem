package com.brunosong.deployment.system.deployment.service.dto.query;

import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;

/**
 * 화면의 커리큘럼 목록 한 줄.
 * 배포 이력(성공한 대상 수, 마지막 배포)은 아직 싣지 않는다. 화면은 값이 없으면 "성공 없음"으로 보여 준다.
 */
public record CurriculumSummaryView(String curriculumId, String title, int subjectCount) {

    public static CurriculumSummaryView from(Curriculum curriculum) {
        int subjectCount = curriculum.grades().stream()
                .flatMap(grade -> grade.semesters().stream())
                .mapToInt(semester -> semester.subjects().size())
                .sum();
        return new CurriculumSummaryView(curriculum.curriculumId(), curriculum.title(), subjectCount);
    }
}
