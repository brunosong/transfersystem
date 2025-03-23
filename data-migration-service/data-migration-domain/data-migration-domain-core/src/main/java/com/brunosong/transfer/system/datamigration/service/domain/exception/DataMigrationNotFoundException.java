package com.brunosong.transfer.system.datamigration.service.domain.exception;

import com.brunosong.transfer.system.domain.exception.DomainException;

public class DataMigrationNotFoundException extends DomainException {

    public DataMigrationNotFoundException(String message) {
        super(message);
    }

    public DataMigrationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
