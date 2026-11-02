package com.brunosong.transfer.system.transfer.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoRepositories(basePackages = "com.brunosong.transfer.system.dataaccess")
@Configuration
public class MongoConfiguration {
}
