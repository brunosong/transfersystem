package com.brunosong.transfer.system.transfer.service.application.exception.handler;

import com.brunosong.transfer.system.application.handler.ErrorDTO;
import com.brunosong.transfer.system.application.handler.GlobalExceptionHandler;
import com.brunosong.transfer.system.transfer.service.exception.MaterialNotFoundException;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.exception.TransferLogNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class TransferGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {MaterialNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(MaterialNotFoundException materialNotFoundException) {
        log.error(materialNotFoundException.getMessage(), materialNotFoundException);
        return error(HttpStatus.BAD_REQUEST, materialNotFoundException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {TransferLogNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFound(TransferLogNotFoundException exception) {
        log.info(exception.getMessage());
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {TransferDomainException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorDTO handleDomain(TransferDomainException exception) {
        log.warn(exception.getMessage());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleInvalid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Invalid request: " + exception.getMessage());
    }

    private static ErrorDTO error(HttpStatus status, String message) {
        return ErrorDTO.builder().code(status.getReasonPhrase()).message(message).build();
    }
}
