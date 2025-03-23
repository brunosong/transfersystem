package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.mapper.DataMigrationDataAccessMapper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.repository.DataMigrationInfoJpaRepository;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.DataMigrationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataMigrationInfoRepositoryImpl implements DataMigrationInfoRepository {

    private final DataMigrationInfoJpaRepository dataMigrationInfoJpaRepository;
    private final DataMigrationDataAccessMapper dataAccessMapper;

    @Override
    public Optional<DataMigration> findById(Long datamigrationId) {
        return dataMigrationInfoJpaRepository.findById(datamigrationId).map( entity ->
                dataAccessMapper.entityToDomain(entity)
        );
    }
}
