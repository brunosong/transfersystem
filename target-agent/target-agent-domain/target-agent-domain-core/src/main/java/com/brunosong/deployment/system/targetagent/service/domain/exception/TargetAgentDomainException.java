package com.brunosong.deployment.system.targetagent.service.domain.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class TargetAgentDomainException extends DomainException {

    public TargetAgentDomainException(String message) {
        super(message);
    }

    public TargetAgentDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
