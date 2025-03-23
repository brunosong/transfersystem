package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.mapper;

import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity.DataMigrationInfoEntity;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationDataAccessMapper {

    public DataMigration entityToDomain(DataMigrationInfoEntity entity) {
        return DataMigration.builder()
                .build();
    }
}
