package com.brunosong.transfer.system.main;

import com.brunosong.transfer.system.kafka.config.data.KafkaConfigData;
import org.apache.avro.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@ActiveProfiles("dev")
//@TestPropertySource(properties = {
//        "transfer-service.data-migration-schema-subjects=avro/data_migration_request.avsc",
//        "kafka-config.schema-registry-url=http://localhost:8081"
//})
//@SpringBootTest(classes = { ConfigurationPropertiesAutoConfiguration.class, SchemaRegistryInitializer.class, KafkaConfigData.class})
class SchemaRegistryInitializerTest {

//    @Autowired
//    KafkaConfigData kafkaConfigData;
//
//    @Autowired
//    SchemaRegistryInitializer schemaRegistryInitializer;
//
//    @Autowired
//    private ResourceLoader resourceLoader;
//
//    @Value("${transfer-service.data-migration-schema-subjects}")
//    private List<String> schemaSubjects;
//
//    @Test
//    @DisplayName("KafkaConfigData가 올바르게 바인딩된다.")
//    void kafkaConfigDataBinding() {
//        assertNotNull(kafkaConfigData.getSchemaRegistryUrl(), "Schema Registry URL should not be null");
//    }
//
//    @Test
//    @DisplayName("avro 파일이 정상적으로 찾아진다.")
//    void findAvroFileSuccess() throws IOException {
//
//        Schema.Parser parser = new Schema.Parser();
//        for (String avroPath : schemaSubjects) {
//            Schema schema = parser.parse(resourceLoader.getResource("classpath:" + avroPath).getInputStream());
//            assertNotNull(schema, "Parsed Avro schema should not be null");
//        }
//
//    }
//
//    @Test
//    @DisplayName("subject 이름은 avro 파일 이름에 value를 조합한다.")
//    void subjectName() {
//        for (String avroPath : schemaSubjects) {
//            String subjectNameFromAvroFile = schemaRegistryInitializer.getSubjectNameFromAvroFile(avroPath);
//            assertEquals("data-migration-request-value", subjectNameFromAvroFile);
//        }
//    }

}