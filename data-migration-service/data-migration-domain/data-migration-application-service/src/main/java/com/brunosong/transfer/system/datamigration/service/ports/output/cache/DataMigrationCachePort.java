package com.brunosong.transfer.system.datamigration.service.ports.output.cache;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;

import java.util.Optional;

public interface DataMigrationCachePort {
    void save(DataMigration dataMigration);

    Optional<DataMigration> findById(Long migrationInfoId);
}
