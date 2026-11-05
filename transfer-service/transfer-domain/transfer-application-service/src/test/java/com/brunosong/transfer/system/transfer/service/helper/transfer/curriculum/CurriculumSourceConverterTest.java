package com.brunosong.transfer.system.transfer.service.helper.transfer.curriculum;

import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.valueobject.ChunkContent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurriculumSourceConverterTest {

    private static final String LESSON = """
            {"id":"L1","title":"차시 1","description":"차시 설명","lessonNumber":1,"learningLevel":3,"extra":"저작에만 있는 값"}""";

    private static final String DOCUMENT = """
            {"curriculumId":"CUR001","title":"중등","description":"중등 과정","grade":[
              {"id":"G1","title":"1학년","semester":[
                {"id":"S1","title":"1학기","description":"학기 설명","subject":[
                  {"id":"SUB1","title":"국어","unit":[{"id":"U1","title":"단원 1","lesson":[%s]}]},
                  {"id":"SUB2","title":"수학","description":"수학 학습","unit":[]}
                ]}
              ]}
            ]}""".formatted(LESSON);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurriculumSourceConverter converter = new CurriculumSourceConverter();

    @Test
    @DisplayName("뼈대에는 학기까지와 학기별 과목 ID 목록이 담긴다")
    void skeleton() throws Exception {
        ChunkContent.Skeleton skeleton = converter.toSkeleton(objectMapper.readTree(DOCUMENT));

        assertEquals("중등", skeleton.title());
        assertEquals("중등 과정", skeleton.description());
        ChunkContent.Grade grade = skeleton.grades().get(0);
        assertNull(grade.description());
        assertEquals(List.of("SUB1", "SUB2"), grade.semesters().get(0).subjectIds());
    }

    @Test
    @DisplayName("과목마다 청크 하나가 되고 차시까지 담긴다")
    void subjectChunks() throws Exception {
        List<ChunkContent.SubjectChunk> chunks = converter.toSubjectChunks(objectMapper.readTree(DOCUMENT));

        assertEquals(2, chunks.size());
        ChunkContent.SubjectChunk first = chunks.get(0);
        assertEquals("G1", first.gradeId());
        assertEquals("S1", first.semesterId());
        assertEquals(new ChunkContent.Lesson("L1", "차시 1", "차시 설명", 1, 3),
                first.subject().units().get(0).lessons().get(0));
        assertTrue(chunks.get(1).subject().units().isEmpty());
    }

    @Test
    @DisplayName("필수 필드가 빠진 문서는 어느 필드인지 알려 주며 실패한다")
    void missingField() throws Exception {
        JsonNode document = objectMapper.readTree(DOCUMENT.replace("\"lessonNumber\":1,", ""));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> converter.toSubjectChunks(document));

        assertTrue(e.getMessage().contains("lessonNumber"));
        assertTrue(e.getMessage().contains("L1"));
    }

    @Test
    // createdAt 은 뺐다. 이 모듈의 테스트에는 시간 모듈이 없고, 실행할 때는 스프링의 ObjectMapper 가 다룬다
    @DisplayName("아웃박스 페이로드는 JSON 으로 저장했다가 읽어도 내용이 같다")
    void payloadRoundTrip() throws Exception {
        ChunkContent.SubjectChunk chunk = converter.toSubjectChunks(objectMapper.readTree(DOCUMENT)).get(0);
        DataTransferEventPayload payload = DataTransferEventPayload.builder()
                .transferId(UUID.randomUUID())
                .targetSystem("ONLINE_CAMPUS")
                .environment("DEV")
                .sourceId("CUR001")
                .subjectChunk(chunk)
                .chunkOffset(1)
                .build();

        DataTransferEventPayload read = objectMapper.readValue(objectMapper.writeValueAsString(payload), DataTransferEventPayload.class);

        ChunkContent content = read.getContent();
        assertEquals(chunk, assertInstanceOf(ChunkContent.SubjectChunk.class, content));
        assertEquals("ONLINE_CAMPUS", read.getTargetSystem());
    }
}
