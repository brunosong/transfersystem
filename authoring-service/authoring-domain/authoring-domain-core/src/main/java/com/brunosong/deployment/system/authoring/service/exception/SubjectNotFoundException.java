package com.brunosong.deployment.system.authoring.service.exception;

/** 그 커리큘럼에 그 ID의 과목이 없다. API 에서는 404 가 된다 */
public class SubjectNotFoundException extends RuntimeException {

    public SubjectNotFoundException(String curriculumId, String subjectId) {
        super("과목이 없다: " + subjectId + " (커리큘럼 " + curriculumId + ")");
    }

}
