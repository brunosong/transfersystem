package com.brunosong.deployment.system.deployment.service.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "deployment-service")
public class DeploymentServiceProperties {
    private String dataMigrationRequestTopicName;
    private String dataMigrationResponseTopicName;
}
