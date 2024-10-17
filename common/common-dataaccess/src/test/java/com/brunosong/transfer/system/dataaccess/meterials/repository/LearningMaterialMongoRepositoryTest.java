package com.brunosong.transfer.system.dataaccess.meterials.repository;

import com.brunosong.transfer.system.dataaccess.TestDataAccessConfiguration;
import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialMongoEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "spring.mongodb.embedded.version=3.4.11"})
@ContextConfiguration(classes = TestDataAccessConfiguration.class)
@DataMongoTest
class LearningMaterialMongoRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void test() {
        LearningMaterialMongoEntity user = new LearningMaterialMongoEntity();
        mongoTemplate.save(user); // 저장
    }
}