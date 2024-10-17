package com.brunosong.transfer.system.dataaccess.meterials.repository;

import com.brunosong.transfer.system.dataaccess.TestDataAccessConfiguration;
import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialMongoEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TestPropertySource(properties = {"spring.data.mongodb.uuid-representation=java_legacy","logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG"})
@ContextConfiguration(classes = TestDataAccessConfiguration.class)
@DataMongoTest
class LearningMaterialMongoRepositoryTest {

    @Autowired
    LearningMaterialMongoRepository learningMaterialMongoRepository;

    @Test
    void 저장_가져오기_테스트() {

        LearningMaterialMongoEntity materialMongo =
                new LearningMaterialMongoEntity(null,"TEST1","내용 TEST", 1,new ArrayList<>());
        learningMaterialMongoRepository.save(materialMongo); // 저장

        List<LearningMaterialMongoEntity> mongoEntities = learningMaterialMongoRepository.findAll();
        System.out.println(mongoEntities.get(0).getId());
        Assertions.assertThat(mongoEntities.get(0).getId()).isNotNull();

        LearningMaterialMongoEntity result = learningMaterialMongoRepository.findById(mongoEntities.get(0).getId().toString()).get();
        Assertions.assertThat(mongoEntities.get(0).getId()).isEqualTo(result.getId());


    }
}