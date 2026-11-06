package com.brunosong.transfer.system.deployment.service.exception;

import com.brunosong.transfer.system.domain.exception.DomainException;

public class DeploymentDomainException extends DomainException {

    public DeploymentDomainException(String message) {
        super(message);
    }

    public DeploymentDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
