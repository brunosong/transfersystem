package com.brunosong.transfer.system.dataaccess.integration.repository;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// @TestPropertySource(properties = {"spring.data.mongodb.uuid-representation=java_legacy",""})
@ActiveProfiles("mongo")
@DataMongoTest
public class LearningMaterialMongoRepositoryIntegrationTest {

    @Autowired
    private LearningMaterialMongoRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private LearningMaterialViewEntity sampleEntity;

    @BeforeEach
    void setUp() {
        // 컬렉션 초기화
        mongoTemplate.dropCollection(LearningMaterialViewEntity.class);

        // 샘플 데이터 준비
        sampleEntity = LearningMaterialViewEntity.builder()
                .id("learning123")
                .title("Introduction to Java")
                .description("Basic Java programming concepts")
                .learningLevel(1)
                .metadataList(List.of(
                        Map.of("attributeName", "John Doe", "attributeValue", "120"),
                        Map.of("attributeName", "PDF", "attributeValue", "English")
                ))
                .build();
    }

    @Test
    @DisplayName("저장 후 저장된 데이터를 가져와서 검증")
    void saveAndFindById_savesAndRetrievesEntity() {
        // Given
        repository.save(sampleEntity);

        // When
        Optional<LearningMaterialViewEntity> found = repository.findById("learning123");

        // Then
        assertThat(found).isPresent();
        LearningMaterialViewEntity entity = found.get();
        assertThat(entity.getId()).isEqualTo("learning123");
        assertThat(entity.getTitle()).isEqualTo("Introduction to Java");
        assertThat(entity.getDescription()).isEqualTo("Basic Java programming concepts");
        assertThat(entity.getLearningLevel()).isEqualTo(1);
        assertThat(entity.getMetadataList())
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(sampleEntity.getMetadataList());
    }

    @Test
    @DisplayName("데이터가 없으면 Optional.empty를 반환한다")
    void findById_whenEntityDoesNotExist_returnsEmpty() {
        // When
        Optional<LearningMaterialViewEntity> found = repository.findById("nonexistent123");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("모든 데이터를 List<LearningMaterialViewEntity> 로 반환한다")
    void saveMultipleEntities_retrievesAll() {
        // Given
        LearningMaterialViewEntity entity2 = LearningMaterialViewEntity.builder()
                .id("learning456")
                .title("Advanced Java")
                .description("In-depth Java techniques")
                .learningLevel(3)
                .metadataList(List.of(
                        Map.of("attributeName", "Jane Smith", "attributeValue", "300")
                ))
                .build();
        repository.saveAll(List.of(sampleEntity, entity2));

        // When
        List<LearningMaterialViewEntity> allEntities = repository.findAll();

        // Then
        assertThat(allEntities).hasSize(2);
        assertThat(allEntities).extracting("id")
                .containsExactlyInAnyOrder("learning123", "learning456");
    }
}
