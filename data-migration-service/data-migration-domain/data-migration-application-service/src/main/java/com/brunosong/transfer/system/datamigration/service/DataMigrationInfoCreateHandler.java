package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;
import com.brunosong.transfer.system.datamigration.service.domain.exception.DataMigrationDomainException;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;
import com.brunosong.transfer.system.datamigration.service.mapper.DataMigrationDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.DataMigrationInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DataMigrationInfoCreateHandler {

    private final DataMigrationInfoRepository dataMigrationInfoRepository;
    private final DataMigrationDataMapper dataMigrationDataMapper;

    public DataMigrationInfoCreateHandler(DataMigrationInfoRepository dataMigrationInfoRepository, DataMigrationDataMapper dataMigrationDataMapper) {
        this.dataMigrationInfoRepository = dataMigrationInfoRepository;
        this.dataMigrationDataMapper = dataMigrationDataMapper;
    }

    @Transactional
    public CreateDataMigrationInfoResponse persistMigrationInfo(CreateDataMigrationInfoCommand createDataMigrationInfoCommand) {

        DataMigrationInfo info =
                dataMigrationDataMapper.createDataMigrationInfoCommandToDataMigrationInfo(createDataMigrationInfoCommand);

        DataMigrationInfo result = saveDataMigrationInfo(info);

        return dataMigrationDataMapper.dataMigrationInfoToCreateDataMigrationInfoResponse(result);
    }

    private DataMigrationInfo saveDataMigrationInfo(DataMigrationInfo dataMigrationInfo) {

        DataMigrationInfo infoResult = dataMigrationInfoRepository.save(dataMigrationInfo);

        if (infoResult == null) {
            log.error("Could not save dataMigrationInfo!");
            throw new DataMigrationDomainException("Could not save dataMigrationInfo!");
        }
        log.info("DataMigrationInfo saved with id : {}" , infoResult.getId().getValue());

        return infoResult;
    }
}
