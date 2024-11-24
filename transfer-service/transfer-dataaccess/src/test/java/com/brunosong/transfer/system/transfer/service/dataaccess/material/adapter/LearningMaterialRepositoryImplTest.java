package com.brunosong.transfer.system.transfer.service.dataaccess.material.adapter;

import com.brunosong.transfer.system.transfer.service.dataaccess.TestDataAccessConfiguration;
import com.brunosong.transfer.system.transfer.service.dataaccess.material.mapper.LearningMaterialDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

//@TestPropertySource(properties = {"spring.data.mongodb.uuid-representation=java_legacy","logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG"})
//@ContextConfiguration(classes = {TestDataAccessConfiguration.class, LearningMaterialRepositoryImpl.class, LearningMaterialDataAccessMapper.class})
//@DataMongoTest
class LearningMaterialRepositoryImplTest {

    @Autowired
    private LearningMaterialRepository repository;

//    @Test
//    void findById() {
//
//        Optional<LearningMaterial> byId = repository.findById("0");
//        Assertions.assertThat(byId).isEmpty();
//
//    }
}