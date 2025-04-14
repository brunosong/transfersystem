package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@SpringJUnitConfig(classes = {CurriculumSourceTypePublisher.class, ExamResultDataSourceTypePublisher.class,
        TransferMessagingSendHelper.class})
class TransferMessagingSendHelperTest {

    @MockBean
    TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @MockBean
    CurriculumSourceTypePublisher CurriculumSourceTypePublisher;

    @MockBean
    ExamResultDataSourceTypePublisher examResultDataSourceTypePublisher;

    @Autowired
    TransferMessagingSendHelper transferMessagingSendHelper;

    @Test
    @DisplayName("SourceType에 따라서 TypePublisher가 선택된다.")
    void sourceTypeCheck() {
        // Given
        String jsonDataStr = """
                {
                    "curriculumEntity": {
                        "curriculumId": "CUR001",
                        "gradeEntity": [
                            {
                                "id": "G0101",
                                "title": "중등 1학년",
                                "description": "중등 1학년 과정",
                                "semesterEntity" : [
                                     {
                                         "id" : "S01201",
                                         "title" : "1학기",
                                         "description" : "중등 2학년 1학기",
                                         "subjectEntity": []
                                     },
                                     {
                                         "id" : "S01202",
                                         "title" : "2학기",
                                         "description" : "중등 2학년 2학기",
                                         "subjectEntity": []
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
        transferMessagingSendHelper.doTransfer(transfer);

        // Then
        // 1. CurriculumSourceTypePublisher가 호출되었는지 확인
        verify(CurriculumSourceTypePublisher, times(1)).typePublisher(transfer);
        // 2. ExamResultDataSourceTypePublisher는 호출되지 않았는지 확인
        verify(examResultDataSourceTypePublisher, never()).typePublisher(any(Transfer.class));

    }

    @Test
    @DisplayName("두개의 타입이 정상적으로 List에 주입되었다")
    void testCheckSourceTypePublishers() throws Exception {
        // 리플렉션으로 private 필드 접근
        Field field = TransferMessagingSendHelper.class.getDeclaredField("sourceTypePublishers");
        field.setAccessible(true);
        List<SourceTypePublisher> publishers = (List<SourceTypePublisher>) field.get(transferMessagingSendHelper);

        // 검증
        assertEquals(2, publishers.size());
        assertTrue(publishers.stream().anyMatch(p -> p instanceof CurriculumSourceTypePublisher));
        assertTrue(publishers.stream().anyMatch(p -> p instanceof ExamResultDataSourceTypePublisher));
    }

}