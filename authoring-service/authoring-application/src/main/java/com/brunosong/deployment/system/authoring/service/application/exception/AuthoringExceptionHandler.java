package com.brunosong.deployment.system.authoring.service.application.exception;

import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 유스케이스가 던진 예외를 HTTP 응답으로 바꾼다 */
@RestControllerAdvice
public class AuthoringExceptionHandler {

    @ExceptionHandler(CurriculumNotFoundException.class)
    public ProblemDetail handleNotFound(CurriculumNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
