package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.valueobject.DbType;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataMongoTest
@SpringBootConfiguration
@EnableAutoConfiguration
@TestPropertySource(properties = {
        "spring.mongodb.embedded.version=4.0.0",
        "spring.data.mongodb.host=localhost",
        "spring.data.mongodb.port=28888",
        "spring.data.mongodb.db=test",
        "logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG"})
@SpringJUnitConfig(classes = DynamicDataSourceConnector.class)
class DynamicDataSourceConnectorTest {

//    @Autowired
//    private DynamicDataSourceConnector connector;
//
//    @Autowired
//    private MongoTemplate mongoTemplate; // 데이터 삽입용
//
//    SourceConfig config;
//    @BeforeEach
//    void setUp() {
//        // 테스트 데이터 삽입
//        Document doc = new Document("_id", new ObjectId("67430b2de9643e5d251265f8"))
//                .append("title", "Test Material");
//        mongoTemplate.insert(doc, "learningMaterialView");
//
//        // DbConfig 모킹
//        config = new SourceConfig(SourceType.LEARNING_MATERIAL, DbType.MONGO, "localhost", 28888, "test",
//                "root", "password", "learningMaterialView");
//    }
//
//    @Test
//    @DisplayName("query 메소드 동작 확인")
//    void query() {
//
//        Optional<Map<String, Object>> result = connector.query(config, "67430b2de9643e5d251265f8");
//
//        assertThat(result).isPresent();
//        assertThat(result.get().get("_id")).isEqualTo(new ObjectId("67430b2de9643e5d251265f8"));
//        assertThat(result.get().get("title")).isEqualTo("Test Material");
//
//    }

}