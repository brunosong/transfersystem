package com.brunosong.deployment.system.authoring.service.exception;

/**
 * 과목이 남아 있는 학기는 지울 수 없다(저작 도메인 모델 6장). API 에서는 422 가 된다.
 * 학기를 지우면 그 학기를 가리키던 과목이 갈 곳을 잃는다. 과목을 먼저 지우거나 다른 학기로 옮긴다
 */
public class SemesterHasSubjectsException extends RuntimeException {

    public SemesterHasSubjectsException(long subjectCount) {
        super("과목이 남아 있어 지울 수 없다. 과목 " + subjectCount + "개를 먼저 지우거나 다른 학기로 옮겨라");
    }
}
