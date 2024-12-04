package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;
import com.brunosong.transfer.system.datamigration.service.ports.input.service.LoadTargetApplicationService;
import org.springframework.stereotype.Service;

@Service
public class LoadTargetApplicationServiceImpl implements LoadTargetApplicationService {

    @Override
    public CreateDataMigrationInfoResponse createLoadTarget(CreateDataMigrationInfoCommand createDataMigrationInfoCommand) {
        return null;
    }
}
