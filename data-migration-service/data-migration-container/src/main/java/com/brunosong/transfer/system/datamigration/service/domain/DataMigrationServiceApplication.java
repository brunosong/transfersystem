package com.brunosong.transfer.system.datamigration.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class DataMigrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationServiceApplication.class, args);
    }
}