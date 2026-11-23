package com.brunosong.deployment.system.authoring.service.exception;

/** 그 과목에 그 ID의 단원이 없다. API 에서는 404 가 된다 */
public class UnitNotFoundException extends RuntimeException {

    public UnitNotFoundException(String subjectId, String unitId) {
        super("단원이 없다: " + unitId + " (과목 " + subjectId + ")");
    }
}
