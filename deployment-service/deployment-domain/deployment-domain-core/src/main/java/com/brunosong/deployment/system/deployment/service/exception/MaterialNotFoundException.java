package com.brunosong.deployment.system.deployment.service.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class MaterialNotFoundException extends DomainException {
    public MaterialNotFoundException(String message) {
        super(message);
    }

    public MaterialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
