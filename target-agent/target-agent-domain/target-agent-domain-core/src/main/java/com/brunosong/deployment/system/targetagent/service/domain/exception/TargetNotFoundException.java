package com.brunosong.deployment.system.targetagent.service.domain.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class TargetNotFoundException extends DomainException {

    public TargetNotFoundException(String message) {
        super(message);
    }

    public TargetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
