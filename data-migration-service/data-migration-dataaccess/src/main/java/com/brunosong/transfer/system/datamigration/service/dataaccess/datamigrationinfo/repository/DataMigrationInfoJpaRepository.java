package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity.DataMigrationInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataMigrationInfoJpaRepository extends JpaRepository<DataMigrationInfoEntity,Long> {
}
