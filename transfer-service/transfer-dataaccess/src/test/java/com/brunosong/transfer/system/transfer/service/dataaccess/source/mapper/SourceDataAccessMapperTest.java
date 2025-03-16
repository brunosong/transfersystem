package com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

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

}