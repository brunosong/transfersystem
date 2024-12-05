package com.brunosong.transfer.system.datamigration.service.ports.input.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;

public interface DataMigrationApplicationService {
      CreateDataMigrationInfoResponse createDataMigrationInfo(CreateDataMigrationInfoCommand createDataMigrationInfoCommand);
}
