package com.brunosong.transfer.system.datamigration.service.domain.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DatabaseEnvironment;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationDestinationService;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationMode;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationDbCredentials;

import java.util.UUID;

public class DataMigrationInfo extends AggregateRoot<DataMigrationInfoId> {

    private final MigrationDestinationService destinationService;
    private final DatabaseEnvironment dbEnvironment;
    private final MigrationMode migrationMode;
    private final DestinationDbCredentials destinationDbCredentials;

    private DataMigrationInfo(Builder builder) {
        setId(builder.dataMigrationInfoId);
        destinationService = builder.destinationService;
        dbEnvironment = builder.dbEnvironment;
        migrationMode = builder.migrationMode;
        destinationDbCredentials = builder.destinationDbCredentials;
    }

    public MigrationDestinationService getDestinationService() {
        return destinationService;
    }

    public void initializeDataMigrationInfo() {
        setId(new DataMigrationInfoId(UUID.randomUUID()));
    }

    public DatabaseEnvironment getDbEnvironment() {
        return dbEnvironment;
    }

    public MigrationMode getMigrationMode() {
        return migrationMode;
    }

    public DestinationDbCredentials getDestinationDbCredentials() {
        return destinationDbCredentials;
    }

    public boolean checkSaved() {
        return getId() != null ? true : false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private DataMigrationInfoId dataMigrationInfoId;
        private MigrationDestinationService destinationService;
        private DatabaseEnvironment dbEnvironment;
        private MigrationMode migrationMode;
        private DestinationDbCredentials destinationDbCredentials;

        private Builder() {}

        public Builder dataMigrationInfoId(DataMigrationInfoId val) {
            dataMigrationInfoId = val;
            return this;
        }

        public Builder destinationService(MigrationDestinationService val) {
            destinationService = val;
            return this;
        }

        public Builder dbEnvironment(DatabaseEnvironment val) {
            dbEnvironment = val;
            return this;
        }

        public Builder migrationMode(MigrationMode val) {
            migrationMode = val;
            return this;
        }

        public Builder destinationDbCredentials(DestinationDbCredentials val) {
            destinationDbCredentials = val;
            return this;
        }

        public DataMigrationInfo build() {
            return new DataMigrationInfo(this);
        }

    }

}
