package com.brunosong.transfer.system.datamigration.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "data-migration-service")
public class DataMigrationServiceConfigData {
    private String materialRequestTopicName;
    private String materialResponseTopicName;
}
