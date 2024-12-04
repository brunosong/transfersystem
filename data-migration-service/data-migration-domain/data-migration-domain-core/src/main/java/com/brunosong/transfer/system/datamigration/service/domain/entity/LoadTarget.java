package com.brunosong.transfer.system.datamigration.service.domain.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.DatabaseEnvironment;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetId;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetServiceType;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetTypeStatus;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DatabaseInfo;

public class LoadTarget extends AggregateRoot<LoadTargetId> {

    private final LoadTargetServiceType targetService;
    private final DatabaseEnvironment dbEnvironment;
    private final LoadTargetTypeStatus type;
    private final DatabaseInfo databaseInfo;

    private LoadTarget(Builder builder) {
        setId(builder.loadTargetId);
        targetService = builder.targetService;
        dbEnvironment = builder.dbEnvironment;
        type = builder.type;
        databaseInfo = builder.databaseInfo;
    }

    public LoadTargetServiceType getTargetService() {
        return targetService;
    }

    public DatabaseEnvironment getTargetDb() {
        return dbEnvironment;
    }

    public LoadTargetTypeStatus getType() {
        return type;
    }

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private LoadTargetId loadTargetId;
        private LoadTargetServiceType targetService;
        private DatabaseEnvironment dbEnvironment;
        private LoadTargetTypeStatus type;
        private DatabaseInfo databaseInfo;

        private Builder() {}

        public Builder loadTargetId(LoadTargetId loadTargetId) {
            loadTargetId = loadTargetId;
            return this;
        }

        public Builder targetService(LoadTargetServiceType targetService) {
            targetService = targetService;
            return this;
        }

        public Builder targetDb(DatabaseEnvironment dbEnvironment) {
            dbEnvironment = dbEnvironment;
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
