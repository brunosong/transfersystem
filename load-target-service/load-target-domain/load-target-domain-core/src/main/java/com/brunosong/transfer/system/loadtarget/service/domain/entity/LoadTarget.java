package com.brunosong.transfer.system.loadtarget.service.domain.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetDbStatus;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetId;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetTypeStatus;
import com.brunosong.transfer.system.loadtarget.service.domain.valueobject.DatabaseInfo;

public class LoadTarget extends AggregateRoot<LoadTargetId> {

    private final String targetService;
    private final LoadTargetDbStatus targetDb;
    private final LoadTargetTypeStatus type;
    private final DatabaseInfo databaseInfo;

    private LoadTarget(Builder builder) {
        setId(builder.loadTargetId);
        targetService = builder.targetService;
        targetDb = builder.targetDb;
        type = builder.type;
        databaseInfo = builder.databaseInfo;
    }

    public String getTargetService() {
        return targetService;
    }

    public LoadTargetDbStatus getTargetDb() {
        return targetDb;
    }

    public LoadTargetTypeStatus getType() {
        return type;
    }

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public static final class Builder {

        private LoadTargetId loadTargetId;
        private String targetService;
        private LoadTargetDbStatus targetDb;
        private LoadTargetTypeStatus type;
        private DatabaseInfo databaseInfo;

        private Builder() {}

        public Builder loadTargetId(LoadTargetId loadTargetId) {
            loadTargetId = loadTargetId;
            return this;
        }

        public Builder targetService(String targetService) {
            targetService = targetService;
            return this;
        }

        public Builder targetDb(LoadTargetDbStatus targetDb) {
            targetDb = targetDb;
            return this;
        }

        public Builder type(LoadTargetTypeStatus type) {
            type = type;
            return this;
        }

        public Builder databaseInfo(DatabaseInfo databaseInfo) {
            databaseInfo = databaseInfo;
            return this;
        }

        public LoadTarget build() {
            return new LoadTarget(this);
        }

    }

}
