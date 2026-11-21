package com.brunosong.deployment.system.authoring.service.exception;

public class RevisionConflictException extends RuntimeException {
    public RevisionConflictException(String curriculumId, int expectedRevision) {
        super("다른 사람이 먼저 고쳤다. 다시 불러온 뒤 고쳐라: " + curriculumId + " (내가 본 revision " + expectedRevision + ")");
    }
}
