package com.brunosong.deployment.system.datamigration.service.domain.exception;

import com.brunosong.deployment.system.domain.exception.DomainException;

public class DataMigrationNotFoundException extends DomainException {

    public DataMigrationNotFoundException(String message) {
        super(message);
    }

    public DataMigrationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
