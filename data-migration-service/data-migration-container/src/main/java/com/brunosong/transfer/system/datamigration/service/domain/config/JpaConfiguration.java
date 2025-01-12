package com.brunosong.transfer.system.datamigration.service.domain.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.brunosong.transfer.system.datamigration.service.dataaccess",
        "com.brunosong.transfer.system.dataaccess"})
@EntityScan(basePackages = {"com.brunosong.transfer.system.datamigration.service.dataaccess",
        "com.brunosong.transfer.system.dataaccess"})
@Configuration
public class JpaConfiguration {
}
