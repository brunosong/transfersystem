package com.brunosong.deployment.system.deployment.service.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class DeploymentNotFoundException extends DomainException {

    public DeploymentNotFoundException(String message) {
        super(message);
    }

    public DeploymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
