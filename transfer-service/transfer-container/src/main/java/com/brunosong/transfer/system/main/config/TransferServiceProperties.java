package com.brunosong.transfer.system.main.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "transfer-service")
public class TransferServiceProperties {
    private String dataMigrationRequestTopicName;
    private String dataMigrationResponseTopicName;
    private List<String> dataMigrationSchemaSubjects;
}
