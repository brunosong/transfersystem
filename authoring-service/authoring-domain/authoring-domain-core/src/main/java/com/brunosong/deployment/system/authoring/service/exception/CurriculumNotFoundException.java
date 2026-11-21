package com.brunosong.deployment.system.authoring.service.exception;

/** 그 ID의 커리큘럼이 없다. API 에서는 404 가 된다 */
public class CurriculumNotFoundException extends RuntimeException {

    public CurriculumNotFoundException(String curriculumId) {
        super("커리큘럼이 없다: " + curriculumId);
    }
}
