package com.brunosong.transfer.system.deployment.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "transfer-service")
public class DeploymentServiceConfigData {
    private String dataMigrationRequestTopicName;
}
