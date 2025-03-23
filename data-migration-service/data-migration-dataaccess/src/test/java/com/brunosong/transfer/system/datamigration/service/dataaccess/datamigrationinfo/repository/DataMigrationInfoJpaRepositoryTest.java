package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.DataAccessSpringbootTestConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(DataAccessSpringbootTestConfiguration.class)
@DataJpaTest
class DataMigrationInfoJpaRepositoryTest {


}