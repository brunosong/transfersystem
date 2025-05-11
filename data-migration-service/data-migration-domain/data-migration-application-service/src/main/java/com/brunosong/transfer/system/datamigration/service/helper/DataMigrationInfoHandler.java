package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.config.RoutingDataSourceContextHolder;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.exception.DataMigrationNotFoundException;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DataSourceType;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.DataMigrationInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataMigrationInfoHandler {

    private final DataMigrationInfoRepository dataMigrationInfoRepository;
    public DataMigration findDataMigrationInfo(Long migrationInfoId) {

        Optional<DataMigration> result = dataMigrationInfoRepository.findById(migrationInfoId);

        if (result.isEmpty()) {
            log.error("Not found DataMigration id : {}", migrationInfoId);
            throw new DataMigrationNotFoundException("Not found sourceContentData id : " + migrationInfoId);
        }

        DataMigration dataMigration = result.get();
        choiceDataSource(dataMigration);

        return dataMigration;
    }

    public void choiceDataSource(DataMigration dataMigrationInfo) {
        if (dataMigrationInfo.getTargetSystem().equals("BRUNOSONG_ONLINE_CAMPUS")) {
            if (dataMigrationInfo.getTargetSystemEnvironment() == TargetSystemEnvironment.PROD) {
                RoutingDataSourceContextHolder.setDataSourceType(DataSourceType.BRUNOSONG_BOOTCAMP_PROD);
            } else {
                RoutingDataSourceContextHolder.setDataSourceType(DataSourceType.BRUNOSONG_BOOTCAMP_DEV);
            }
        }
    }

}
