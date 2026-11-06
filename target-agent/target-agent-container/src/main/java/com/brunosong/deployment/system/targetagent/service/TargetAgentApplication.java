package com.brunosong.deployment.system.targetagent.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.brunosong.deployment.system")
public class TargetAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(TargetAgentApplication.class, args);
    }
}