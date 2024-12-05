package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.DataAccessSpringbootTestConfiguration;
import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity.DataMigrationInfoEntity;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationDbEnvironment;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationServiceStatus;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationMode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@Import(DataAccessSpringbootTestConfiguration.class)
@DataJpaTest
class DataMigrationInfoJpaRepositoryTest {

    @Autowired
    private DataMigrationInfoJpaRepository dataMigrationInfoJpaRepository;

    @Test
    void save() {
        // given
        UUID uuid = UUID.randomUUID();
        DataMigrationInfoEntity entity = DataMigrationInfoEntity.builder()
                .id(uuid)
                .dbEnvironment(DestinationDbEnvironment.PRODUCTION)
                .migrationMode(MigrationMode.DB)
                .destinationService(DestinationServiceStatus.AI)
                .build();

        // when
        dataMigrationInfoJpaRepository.save(entity);
        DataMigrationInfoEntity result = dataMigrationInfoJpaRepository.findById(uuid).get();

        // then
        Assertions.assertThat(result.getId()).isEqualTo(uuid);

    }

}