package com.brunosong.transfer.system.transfer.service.dataaccess.source.adapter;

import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;

//@TestPropertySource(properties = {"spring.data.mongodb.uuid-representation=java_legacy","logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG"})
//@ContextConfiguration(classes = {TestDataAccessConfiguration.class, LearningMaterialRepositoryImpl.class, LearningMaterialDataAccessMapper.class})
//@DataMongoTest
class LearningMaterialRepositoryImplTest {

    @Autowired
    private SourceRepository repository;

//    @Test
//    void findById() {
//
//        Optional<LearningMaterial> byId = repository.findById("0");
//        Assertions.assertThat(byId).isEmpty();
//
//    }
}