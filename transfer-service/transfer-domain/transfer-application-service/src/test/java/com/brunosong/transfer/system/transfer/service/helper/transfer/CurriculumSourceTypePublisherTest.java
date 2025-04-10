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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumSourceTypePublisherTest {
    @Mock
    private TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @InjectMocks
    private CurriculumSourceTypePublisher curriculumSourceTypePublisher;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDoTransfer_CurriculumSourceType_Success() throws Exception {
        // Given
        String jsonDataStr = """
                {
                    "curriculum": {
                        "curriculumId": "CUR001",
                        "grade": [
                            {
                                "id": "G0101",
                                "title": "중등 1학년",
                                "description": "중등 1학년 과정",
                                "semester" : [
                                     {
                                         "id" : "S01201",
                                         "title" : "1학기",
                                         "description" : "중등 2학년 1학기",
                                         "subject": []
                                     },
                                     {
                                         "id" : "S01202",
                                         "title" : "2학기",
                                         "description" : "중등 2학년 2학기",
                                         "subject": []
                                     }
                                ]
                            }
                        ]
                    }
                }
                """;
        byte[] jsonData = jsonDataStr.getBytes();

        SourceContentData sourceContentData = SourceContentData.builder()
                .sourceType(SourceType.CURRICULUM)
                .jsonData(jsonData)
                .build();

        Transfer transfer = Transfer.builder()
                .sourceContentData(sourceContentData)
                .build();

        // When
        curriculumSourceTypePublisher.typePublisher(transfer);

        // Then
        verify(transferDataSendMessagePublisher, times(1)).publish(transfer);

        // 상위 데이터가 올바르게 생성되었는지 확인
        byte[] updatedData = sourceContentData.getJsonData();
//        String updatedDataStr = new String(updatedData, StandardCharsets.UTF_8);
//        System.out.println(updatedDataStr);
        JsonNode updatedNode = objectMapper.readTree(updatedData);
        assertEquals("CUR001", updatedNode.path("curriculumId").asText());
        JsonNode grades = updatedNode.path("grades");
        assertEquals(1, grades.size());
        JsonNode grade = grades.get(0);
        assertEquals("G0101", grade.path("id").asText());
        JsonNode semesters = grade.path("semester");
        assertEquals(2, semesters.size());
        JsonNode semester = semesters.get(0);
        assertEquals("S01201", semester.path("id").asText());
        assertFalse(semester.has("subject")); // subject가 제외되었는지 확인
    }
}