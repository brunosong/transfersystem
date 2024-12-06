package com.brunosong.transfer.system.domain.valueobject;

import com.brunosong.transfer.system.domain.valueobject.BaseId;

import java.util.UUID;

public class DataMigrationInfoId extends BaseId<UUID> {

    public DataMigrationInfoId(UUID value) {
        super(value);
    }

}
