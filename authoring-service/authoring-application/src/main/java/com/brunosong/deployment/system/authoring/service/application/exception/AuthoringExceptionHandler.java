package com.brunosong.deployment.system.authoring.service.application.exception;

import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.GradeNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
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
            SubjectNotFoundException.class, UnitNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** 낙관적 락에 걸렸다. 화면은 다시 불러온 뒤 고치게 한다 */
    @ExceptionHandler(RevisionConflictException.class)
    public ProblemDetail handleConflict(RevisionConflictException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
