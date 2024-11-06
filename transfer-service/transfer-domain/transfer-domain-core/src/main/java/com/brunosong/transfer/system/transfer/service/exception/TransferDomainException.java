package com.brunosong.transfer.system.transfer.service.exception;

import com.brunosong.transfer.system.domain.exception.DomainException;

public class TransferDomainException extends DomainException {

    public TransferDomainException(String message) {
        super(message);
    }

    public TransferDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
