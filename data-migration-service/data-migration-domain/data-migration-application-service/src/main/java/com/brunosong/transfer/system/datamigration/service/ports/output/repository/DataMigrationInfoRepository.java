package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;

import java.util.Optional;

public interface DataMigrationInfoRepository {
    Optional<DataMigrationInfo> findById(Long datamigrationId);
}
