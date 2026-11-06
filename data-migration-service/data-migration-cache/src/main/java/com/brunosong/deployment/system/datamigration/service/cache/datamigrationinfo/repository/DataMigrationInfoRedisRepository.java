package com.brunosong.deployment.system.datamigration.service.cache.datamigrationinfo.repository;

import com.brunosong.deployment.system.datamigration.service.cache.datamigrationinfo.entity.DataMigrationInfoCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface DataMigrationInfoRedisRepository extends CrudRepository<DataMigrationInfoCacheEntity, Long> {
}
