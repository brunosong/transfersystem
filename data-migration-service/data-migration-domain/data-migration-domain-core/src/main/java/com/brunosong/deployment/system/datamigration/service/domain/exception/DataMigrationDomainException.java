package com.brunosong.deployment.system.datamigration.service.domain.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class DataMigrationDomainException extends DomainException {

    public DataMigrationDomainException(String message) {
        super(message);
    }

    public DataMigrationDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
