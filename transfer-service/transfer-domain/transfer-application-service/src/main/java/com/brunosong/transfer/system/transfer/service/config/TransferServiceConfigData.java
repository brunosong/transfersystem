package com.brunosong.transfer.system.transfer.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "transfer-service")
public class TransferServiceConfigData {
    private String materialRequestTopicName;
    private String dataMigrationRequestTopicName;
}
