package com.brunosong.deployment.system.deployment.service.dataaccess;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;

@EnableMongoRepositories(basePackages = "com.brunosong.deployment.system.dataaccess")
public class TestDataAccessConfiguration {


}
