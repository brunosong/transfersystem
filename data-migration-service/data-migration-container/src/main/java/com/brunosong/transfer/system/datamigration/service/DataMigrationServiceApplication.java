package com.brunosong.transfer.system.datamigration.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class DataMigrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationServiceApplication.class, args);
    }
}