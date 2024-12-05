package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.brunosong.transfer.system.datamigration.service.dataaccess",
                                       "com.brunosong.transfer.system.dataaccess"})
@EntityScan(basePackages = {"com.brunosong.transfer.system.datamigration.service.dataaccess",
                            "com.brunosong.transfer.system.dataaccess"})
@SpringBootConfiguration
public class DataAccessSpringbootTestConfiguration {
}
