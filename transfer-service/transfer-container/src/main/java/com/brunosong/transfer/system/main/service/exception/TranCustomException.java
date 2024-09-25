package com.brunosong.transfer.system.main.service.exception;

public class TranCustomException extends RuntimeException {

    public TranCustomException(String message) {
        super(message);
    }

    public TranCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
