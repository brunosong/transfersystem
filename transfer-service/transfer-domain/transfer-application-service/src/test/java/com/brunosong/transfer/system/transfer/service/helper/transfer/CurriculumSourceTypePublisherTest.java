package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//@Import({CurriculumSourceTypePublisherTest.TestConfig.class, CurriculumSourceTypePublisher.class})
//@SpringJUnitConfig
class CurriculumSourceTypePublisherTest {
//
//    @TestConfiguration
//    public static class TestConfig {
//        @Bean
//        public ObjectMapper objectMapper() {
//            return new ObjectMapper();
//        }
//    }
//
//    @MockBean
//    private TransferDataSendMessagePublisher transferDataSendMessagePublisher;
//
//    @Autowired
//    private CurriculumSourceTypePublisher curriculumSourceTypePublisher;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void testDoTransfer_CurriculumSourceType_Success() throws Exception {
//        // Given
//        String jsonDataStr = """
//                {
//                    "curriculum": {
//                        "curriculumId": "CUR001",
//                        "gradeEntity": [
//                            {
//                                "id": "G0101",
//                                "title": "중등 1학년",
//                                "description": "중등 1학년 과정",
//                                "semesterEntity" : [
//                                     {
//                                         "id" : "S01201",
//                                         "title" : "1학기",
//                                         "description" : "중등 2학년 1학기",
//                                         "subjectEntity": []
//                                     },
//                                     {
//                                         "id" : "S01202",
//                                         "title" : "2학기",
//                                         "description" : "중등 2학년 2학기",
//                                         "subjectEntity": []
//                                     }
//                                ]
//                            }
//                        ]
//                    }
//                }
//                """;
//        byte[] jsonData = jsonDataStr.getBytes();
//
//        SourceContentData sourceContentData = SourceContentData.builder()
//                .sourceType(SourceType.CURRICULUM)
//                .jsonData(jsonData)
//                .build();
//
//        Transfer transfer = Transfer.builder()
//                .sourceContentData(sourceContentData)
//                .build();
//
//        // When
//        curriculumSourceTypePublisher.typePublisher(transfer);
//
//        // Then
//        verify(transferDataSendMessagePublisher, times(1)).publish(transfer);
//
//        // 상위 데이터가 올바르게 생성되었는지 확인
//        byte[] updatedData = sourceContentData.getJsonData();
//        JsonNode updatedNode = objectMapper.readTree(updatedData);
//        assertEquals("CUR001", updatedNode.path("curriculumId").asText());
//
//        JsonNode grades = updatedNode.path("grades");
//        assertEquals(1, grades.size());
//        JsonNode gradeEntity = grades.get(0);
//        assertEquals("G0101", gradeEntity.path("id").asText());
//        JsonNode semesters = gradeEntity.path("semesterEntity");
//        assertEquals(2, semesters.size());
//        JsonNode semesterEntity = semesters.get(0);
//        assertEquals("S01201", semesterEntity.path("id").asText());
//        assertFalse(semesterEntity.has("subjectEntity")); // subject가 제외되었는지 확인
//    }
}