package com.brunosong.transfer.system.deployment.service.exception;

import com.brunosong.transfer.system.domain.exception.DomainException;

public class DeploymentNotFoundException extends DomainException {

    public DeploymentNotFoundException(String message) {
        super(message);
    }

    public DeploymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
