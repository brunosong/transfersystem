package com.brunosong.transfer.system.datamigration.service.mapper;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationDbCredentials;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DatabaseEnvironment;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationDestinationService;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationMode;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoCommand;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationDataMapper {

    public DataMigrationInfo createDataMigrationInfoCommandToDataMigrationInfo(CreateDataMigrationInfoCommand command) {
        return DataMigrationInfo.builder()
                .destinationService(MigrationDestinationService.valueOf(command.getDestinationService()))
                .dbEnvironment(DatabaseEnvironment.valueOf(command.getDbEnvironment()))
                .migrationMode(MigrationMode.valueOf(command.getMigrationMode()))
                .destinationDbCredentials(new DestinationDbCredentials(command.getDbCredentials().getDbUrl(),
                        command.getDbCredentials().getUserName(), command.getDbCredentials().getPassword()))
                .build();
    }

    public CreateDataMigrationInfoResponse dataMigrationInfoToCreateDataMigrationInfoResponse(DataMigrationInfo dataMigrationInfo) {
        return new CreateDataMigrationInfoResponse(dataMigrationInfo.getId().getValue(), dataMigrationInfo.checkSaved());
    }
}
