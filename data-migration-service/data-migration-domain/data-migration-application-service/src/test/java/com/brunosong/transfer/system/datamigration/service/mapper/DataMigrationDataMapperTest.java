package com.brunosong.transfer.system.datamigration.service.mapper;


import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigrationInfo;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationDbEnvironment;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationServiceStatus;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationMode;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateDataMigrationInfoResponse;
import com.brunosong.transfer.system.datamigration.service.dto.create.DbCredentials;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

@SpringJUnitConfig(classes = { DataMigrationDataMapper.class })
class DataMigrationDataMapperTest {

    @Autowired
    DataMigrationDataMapper dataMigrationDataMapper;

    @Test
    @DisplayName("CreateDataMigrationInfoCommand -> DataMigrationInfo 변환 정상처리")
    void mapper_1() {
        // given
        CreateDataMigrationInfoCommand command = new CreateDataMigrationInfoCommand();
        command.setDestinationService("AI");
        command.setDbEnvironment("DEVELOPMENT");
        command.setMigrationMode("DB");
        command.setDbCredentials(new DbCredentials());

        // when
        DataMigrationInfo info =
                dataMigrationDataMapper.createDataMigrationInfoCommandToDataMigrationInfo(command);

        // then
        Assertions.assertThat(info.getDestinationService()).isEqualTo(DestinationServiceStatus.AI);
        Assertions.assertThat(info.getDbEnvironment()).isEqualTo(DestinationDbEnvironment.DEVELOPMENT);
        Assertions.assertThat(info.getMigrationMode()).isEqualTo(MigrationMode.DB);

    }


    @Test
    @DisplayName("DataMigrationInfo -> CreateDataMigrationInfoResponse 변환 정상처리")
    void mapper_2() {
        // given
        DataMigrationInfoId id = new DataMigrationInfoId(UUID.randomUUID());
        DataMigrationInfo info = DataMigrationInfo.builder()
                .dataMigrationInfoId(id)
                .build();

        // when
        CreateDataMigrationInfoResponse response =
                dataMigrationDataMapper.dataMigrationInfoToCreateDataMigrationInfoResponse(info);

        // then
        Assertions.assertThat(response.id()).isEqualTo(id.getValue());
        Assertions.assertThat(response.saved()).isTrue();

    }

}