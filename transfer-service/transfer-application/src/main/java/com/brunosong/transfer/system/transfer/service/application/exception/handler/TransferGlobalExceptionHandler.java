package com.brunosong.transfer.system.transfer.service.application.exception.handler;

import com.brunosong.transfer.system.application.handler.ErrorDTO;
import com.brunosong.transfer.system.application.handler.GlobalExceptionHandler;
import com.brunosong.transfer.system.transfer.service.exception.MaterialNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class TransferGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {MaterialNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(MaterialNotFoundException materialNotFoundException) {
        log.error(materialNotFoundException.getMessage(), materialNotFoundException);
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(materialNotFoundException.getMessage())
                .build();
    }

}
