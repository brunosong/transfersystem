package com.brunosong.deployment.system.deployment.service.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class DeploymentDomainException extends DomainException {

    public DeploymentDomainException(String message) {
        super(message);
    }

    public DeploymentDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
