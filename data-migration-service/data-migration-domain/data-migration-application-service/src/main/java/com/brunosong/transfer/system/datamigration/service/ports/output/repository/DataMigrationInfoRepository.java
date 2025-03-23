package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;

import java.util.Optional;

public interface DataMigrationInfoRepository {
    Optional<DataMigration> findById(Long datamigrationId);
}
