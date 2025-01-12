package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;

public interface DataMigrationInfoRepository {
    DataMigrationInfo save(DataMigrationInfo dataMigrationInfo);
}
