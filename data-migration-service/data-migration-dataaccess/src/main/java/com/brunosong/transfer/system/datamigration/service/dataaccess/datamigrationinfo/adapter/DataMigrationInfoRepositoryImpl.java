package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.adapter;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.DataMigrationInfoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DataMigrationInfoRepositoryImpl implements DataMigrationInfoRepository {

    @Override
    public DataMigrationInfo save(DataMigrationInfo dataMigrationInfo) {
        return null;
    }
}
