package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.mapper;

import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity.DataMigrationInfoEntity;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationDataAccessMapper {

    public DataMigrationInfo entityToDomain(DataMigrationInfoEntity entity) {
        return DataMigrationInfo.builder()
                .build();
    }
}
