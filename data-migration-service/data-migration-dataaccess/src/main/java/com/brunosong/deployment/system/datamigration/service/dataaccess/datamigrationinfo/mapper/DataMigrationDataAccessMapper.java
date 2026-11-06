package com.brunosong.deployment.system.datamigration.service.dataaccess.datamigrationinfo.mapper;

import com.brunosong.deployment.system.datamigration.service.dataaccess.datamigrationinfo.entity.DataMigrationInfoEntity;
import com.brunosong.deployment.system.datamigration.service.domain.entity.DataMigration;
import org.springframework.stereotype.Component;

@Component
public class
DataMigrationDataAccessMapper {

    public DataMigration entityToDomain(DataMigrationInfoEntity entity) {
        return DataMigration.builder()
                .targetSystem(entity.getTargetSystem())
                .targetSystemEnvironment(entity.getTargetSystemEnvironment())
                .build();
    }
}
