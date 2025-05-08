package com.brunosong.transfer.system.transfer.service.exception;

import com.brunosong.transfer.system.domain.exception.DomainException;

public class TransferLogNotFoundException extends DomainException {

    public TransferLogNotFoundException(String message) {
        super(message);
    }

    public TransferLogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
