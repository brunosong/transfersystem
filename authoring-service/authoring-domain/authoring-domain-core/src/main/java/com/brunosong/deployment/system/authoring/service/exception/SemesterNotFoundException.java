package com.brunosong.deployment.system.authoring.service.exception;

/** 그 ID의 학기가 없다(학년을 알면 학년도 알린다). API 에서는 404 가 된다 */
public class SemesterNotFoundException extends RuntimeException {

    public SemesterNotFoundException(String gradeId, String semesterId) {
        super("학기가 없다: " + semesterId + " (학년 " + gradeId + ")");
    }

    public SemesterNotFoundException(String semesterId) {
        super("학기가 없다: " + semesterId);
    }
}
