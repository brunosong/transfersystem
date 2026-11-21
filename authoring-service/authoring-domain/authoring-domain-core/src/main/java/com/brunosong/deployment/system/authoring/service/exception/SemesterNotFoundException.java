package com.brunosong.deployment.system.authoring.service.exception;

/** 그 학년에 그 ID의 학기가 없다. API 에서는 404 가 된다 */
public class SemesterNotFoundException extends RuntimeException {

    public SemesterNotFoundException(String gradeId, String semesterId) {
        super("학기가 없다: " + semesterId + " (학년 " + gradeId + ")");
    }
}
