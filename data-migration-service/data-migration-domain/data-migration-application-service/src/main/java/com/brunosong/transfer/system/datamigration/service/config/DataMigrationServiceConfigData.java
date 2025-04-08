package com.brunosong.transfer.system.datamigration.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "data-migration-service")
public class DataMigrationServiceConfigData {
    private String dataMigrationRequestTopicName;
    private String dataMigrationResponseTopicName;
    private List<String> targetServers;
}
