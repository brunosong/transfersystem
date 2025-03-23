package com.brunosong.transfer.system.datamigration.service.mapper;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationDataMapper {

//    public DataMigrationInfo createDataMigrationInfoCommandToDataMigrationInfo(CreateDataMigrationInfoCommand command) {
//        return DataMigrationInfo.builder()
//                .destinationService(TargetSystem.valueOf(command.getDestinationService()))
//                .dbEnvironment(DestinationDbEnvironment.valueOf(command.getDbEnvironment()))
//                .migrationMode(MigrationMode.valueOf(command.getMigrationMode()))
//                .destinationDbCredentials(new DestinationDbCredentials(command.getDbCredentials().getDbUrl(),
//                        command.getDbCredentials().getUserName(), command.getDbCredentials().getPassword()))
//                .build();
//    }

//    public CreateDataMigrationInfoResponse dataMigrationInfoToCreateDataMigrationInfoResponse(DataMigrationInfo dataMigrationInfo) {
//        return new CreateDataMigrationInfoResponse(dataMigrationInfo.getId().getValue(), dataMigrationInfo.checkSaved());
//    }
}
