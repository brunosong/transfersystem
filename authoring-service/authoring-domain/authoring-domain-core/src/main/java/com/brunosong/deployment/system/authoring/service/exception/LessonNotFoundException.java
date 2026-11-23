package com.brunosong.deployment.system.authoring.service.exception;

/** 그 단원에 그 ID의 차시가 없다. API 에서는 404 가 된다 */
public class LessonNotFoundException extends RuntimeException {

    public LessonNotFoundException(String unitId, String lessonId) {
        super("차시가 없다: " + lessonId + " (단원 " + unitId + ")");
    }
}
