package com.brunosong.deployment.system.targetagent.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "target-agent")
public class TargetAgentConfigData {
    private String dataMigrationRequestTopicName;
    private String dataMigrationResponseTopicName;
    private List<String> targetServers;
}
