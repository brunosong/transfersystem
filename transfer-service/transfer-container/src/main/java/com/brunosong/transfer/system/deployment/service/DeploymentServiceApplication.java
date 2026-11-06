package com.brunosong.transfer.system.deployment.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class DeploymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeploymentServiceApplication.class, args);
    }
}
