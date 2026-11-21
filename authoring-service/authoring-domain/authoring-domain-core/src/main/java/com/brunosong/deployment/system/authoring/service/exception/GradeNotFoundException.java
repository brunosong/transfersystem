package com.brunosong.deployment.system.authoring.service.exception;

/** 초안에 그 ID의 학년이 없다. API 에서는 404 가 된다 */
public class GradeNotFoundException extends RuntimeException {

    public GradeNotFoundException(String gradeId) {
        super("학년이 없다: " + gradeId);
    }
}
