package com.brunosong.deployment.system.authoring.service.application.exception;

import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.GradeNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.LessonNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.PublishRejectedException;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
import com.brunosong.deployment.system.authoring.service.exception.SemesterHasSubjectsException;
import com.brunosong.deployment.system.authoring.service.exception.SemesterNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.SubjectNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.UnitNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 유스케이스가 던진 예외를 HTTP 응답으로 바꾼다 */
@RestControllerAdvice
public class AuthoringExceptionHandler {

    @ExceptionHandler({CurriculumNotFoundException.class, GradeNotFoundException.class, SemesterNotFoundException.class,
            SubjectNotFoundException.class, UnitNotFoundException.class,
            LessonNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * 규칙 때문에 할 수 없다(과목이 남은 학기 지우기, 바뀐 것이 없거나 어긋난 초안 공개). 409 는 "다시 불러와라"라는 뜻으로 쓰고 있어서 422 로 나눈다.
     * 다시 불러와도 같은 결과라, 화면은 이유를 보여 주기만 한다
     */
    @ExceptionHandler({SemesterHasSubjectsException.class, PublishRejectedException.class})
    public ProblemDetail handleRuleViolation(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    /**
     * 도메인이 값을 거절했다(빈 제목, 1~5 밖의 학습 수준 등). API 검사를 지나 도메인에서 걸려도 400 이 되게 한다
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleInvalid(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** 낙관적 락에 걸렸다. 화면은 다시 불러온 뒤 고치게 한다 */
    @ExceptionHandler(RevisionConflictException.class)
    public ProblemDetail handleConflict(RevisionConflictException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
