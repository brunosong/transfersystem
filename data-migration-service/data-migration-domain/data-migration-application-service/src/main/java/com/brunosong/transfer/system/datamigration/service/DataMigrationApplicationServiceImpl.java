package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;
import com.brunosong.transfer.system.datamigration.service.ports.input.service.DataMigrationApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataMigrationApplicationServiceImpl implements DataMigrationApplicationService {

    private final DataMigrationInfoCreateHandler dataMigrationInfoCreateHandler;

    public DataMigrationApplicationServiceImpl(DataMigrationInfoCreateHandler dataMigrationInfoCreateHandler) {
        this.dataMigrationInfoCreateHandler = dataMigrationInfoCreateHandler;
    }

    @Override
    public CreateDataMigrationInfoResponse createDataMigrationInfo(CreateDataMigrationInfoCommand createDataMigrationInfoCommand) {
        return dataMigrationInfoCreateHandler.persistMigrationInfo(createDataMigrationInfoCommand);
    }

}
