package com.brunosong.transfer.system.transfer.service.dataaccess.source.adapter;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialMongoRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper.SourceDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SourceRepositoryImplTest {
//
//    @Mock
//    private LearningMaterialMongoRepository learningMaterialMongoRepository;
//
//    @Mock
//    private SourceDataAccessMapper sourceDataAccessMapper;
//
//    @InjectMocks
//    private SourceRepositoryImpl sourceRepository;
//
//    private LearningMaterialViewEntity entity;
//    private SourceContentData contentData;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() throws JsonProcessingException {
//
//        objectMapper = new ObjectMapper();
//
//        // 샘플 데이터 초기화
//        entity = LearningMaterialViewEntity.builder()
//                .id("learning123")
//                .title("Introduction to Java")
//                .description("Basic Java programming concepts")
//                .learningLevel(1)
//                .metadataList(List.of(
//                        Map.of("attributeName", "John Doe", "attributeValue", "120"),
//                        Map.of("attributeName", "PDF", "attributeValue", "English")
//                ))
//                .build();
//
//        String jsonString = objectMapper.writeValueAsString(entity);
//        byte[] jsonData = jsonString.getBytes(StandardCharsets.UTF_8);
//
//        contentData = SourceContentData.builder()
//                .sourceId("learning123")
//                .jsonData(jsonData)
//                .build();
//    }
//
//    @Test
//    @DisplayName("자료가 존재하면 findData가 SourceContentData를 반환한다")
//    void findData() {
//        // Given
////        String materialId = "learning123";
////        when(learningMaterialMongoRepository.findById(materialId)).thenReturn(Optional.of(entity));
////        when(sourceDataAccessMapper.mapToSourceContentData(entity)).thenReturn(contentData);
////
////        // When
////        Optional<SourceContentData> result = sourceRepository.findData(materialId, SourceType.LEARNING_MATERIAL);
////
////
////        // Then
////        assertTrue(result.isPresent());
////        SourceContentData resultData = result.get();
////        assertEquals(materialId, resultData.getSourceId());
////        assertArrayEquals(contentData.getJsonData(), resultData.getJsonData());
////
////        verify(learningMaterialMongoRepository).findById(materialId);
////        verify(sourceDataAccessMapper).mapToSourceContentData(entity);
//    }
//
//    @Test
//    @DisplayName("자료가 존재하지 않으면 Optional.map은 실행되지 않으며 Optional.empty()가 반환된다")
//    void findData_returnEmpty() {
//
//        // Given
//        String materialId = "learning123";
//        when(learningMaterialMongoRepository.findById(materialId)).thenReturn(Optional.empty());
//
//        // When
////        Optional<SourceContentData> data = sourceRepository.findData(materialId, SourceType.LEARNING_MATERIAL);
////
////        // Then
////        assertThat(data).isEmpty();
////
////        verify(learningMaterialMongoRepository).findById(materialId);
////        verifyNoInteractions(sourceDataAccessMapper);  // map 호출 안 됨 확인
//    }

}