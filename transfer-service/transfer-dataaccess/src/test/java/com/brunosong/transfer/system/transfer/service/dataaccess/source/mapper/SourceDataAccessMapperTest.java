package com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SourceDataAccessMapperTest {

    private LearningMaterialViewEntity entity;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

        objectMapper = new ObjectMapper();

        // 샘플 엔티티 초기화
        entity = LearningMaterialViewEntity.builder()
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
    @DisplayName("mongodb 에서 byte[] 로 정상 변환")
    void entityViewToSourceContentData() throws JsonProcessingException {

        // Given
        SourceDataAccessMapper mapper = new SourceDataAccessMapper();

        // When
        SourceContentData data = mapper.entityViewToSourceContentData(entity);

        // Then
        // 1. sourceId 검증
        assertThat(data.getSourceId()).isEqualTo("learning123");

        // 2. jsonData를 파싱해 내용 검증
        String jsonString = new String(data.getJsonData(), StandardCharsets.UTF_8);

        // 3. 검증을 위해 String -> 객체로 변환
        LearningMaterialViewEntity parsedEntity = objectMapper.readValue(jsonString, LearningMaterialViewEntity.class);

        assertThat(parsedEntity.getId()).isEqualTo(entity.getId());
        assertThat(parsedEntity.getTitle()).isEqualTo(entity.getTitle());
        assertThat(parsedEntity.getDescription()).isEqualTo(entity.getDescription());
        assertThat(parsedEntity.getLearningLevel()).isEqualTo(entity.getLearningLevel());
        assertThat(parsedEntity.getMetadataList())
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(entity.getMetadataList());
    }
}