package com.brunosong.transfer.system.datamigration.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system.datamigration")
public class DataMigrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationServiceApplication.class, args);
    }
}