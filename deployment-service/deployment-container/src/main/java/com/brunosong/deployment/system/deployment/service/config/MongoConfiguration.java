package com.brunosong.deployment.system.deployment.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoRepositories(basePackages = "com.brunosong.deployment.system.dataaccess")
@Configuration
public class MongoConfiguration {
}
