package com.brunosong.deployment.system.datamigration.service.dataaccess.datamigrationinfo;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.brunosong.deployment.system.datamigration.service.dataaccess",
                                       "com.brunosong.deployment.system.dataaccess"})
@EntityScan(basePackages = {"com.brunosong.deployment.system.datamigration.service.dataaccess",
                            "com.brunosong.deployment.system.dataaccess"})
@SpringBootConfiguration
public class DataAccessSpringbootTestConfiguration {
}
