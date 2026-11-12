package com.brunosong.deployment.system.deployment.service.helper.deployment.curriculum;

import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
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
    @DisplayName("저작 문서가 학년부터 차시까지 커리큘럼 트리로 옮겨진다")
    void curriculum() throws Exception {
        Curriculum curriculum = converter.toCurriculum(objectMapper.readTree(DOCUMENT));

        assertEquals("CUR001", curriculum.curriculumId());
        assertEquals("중등", curriculum.title());
        assertEquals("중등 과정", curriculum.description());
        Curriculum.Grade grade = curriculum.grades().get(0);
        assertNull(grade.description());
        Curriculum.Semester semester = grade.semesters().get(0);
        assertEquals("학기 설명", semester.description());
        assertEquals(2, semester.subjects().size());
        assertTrue(semester.subjects().get(1).units().isEmpty());
    }

    @Test
    @DisplayName("아는 칸을 뺀 나머지 원본 칸은 단원과 차시의 attributes에 JSON으로 모인다")
    void attributes() throws Exception {
        Curriculum curriculum = converter.toCurriculum(objectMapper.readTree(DOCUMENT));

        Curriculum.Unit unit = curriculum.grades().get(0).semesters().get(0).subjects().get(0).units().get(0);
        assertEquals("{}", unit.attributes());
        assertEquals(new Curriculum.Lesson("L1", "차시 1", "차시 설명", 1, 3,
                        "{\"learningLevel\":3,\"extra\":\"저작에만 있는 값\"}"),
                unit.lessons().get(0));
    }

    @Test
    @DisplayName("옮긴 커리큘럼을 나누면 옮기기 전처럼 뼈대 하나와 과목마다 청크 하나가 나온다")
    void splitAfterConvert() throws Exception {
        List<ChunkContent> chunks = ChunkContent.split(converter.toCurriculum(objectMapper.readTree(DOCUMENT)));

        assertEquals(3, chunks.size());
        ChunkContent.Skeleton skeleton = assertInstanceOf(ChunkContent.Skeleton.class, chunks.getFirst());
        assertEquals("중등", skeleton.title());
        assertEquals(List.of("SUB1", "SUB2"), skeleton.grades().get(0).semesters().get(0).subjectIds());
        ChunkContent.SubjectChunk first = assertInstanceOf(ChunkContent.SubjectChunk.class, chunks.get(1));
        assertEquals("G1", first.gradeId());
        assertEquals("S1", first.semesterId());
        assertEquals("SUB1", first.subject().id());
    }

    @Test
    @DisplayName("필수 필드가 빠진 문서는 어느 필드인지 알려 주며 실패한다")
    void missingField() throws Exception {
        JsonNode document = objectMapper.readTree(DOCUMENT.replace("\"lessonNumber\":1,", ""));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> converter.toCurriculum(document));

        assertTrue(e.getMessage().contains("lessonNumber"));
        assertTrue(e.getMessage().contains("L1"));
    }

    @Test
    // createdAt 은 뺐다. 이 모듈의 테스트에는 시간 모듈이 없고, 실행할 때는 스프링의 ObjectMapper 가 다룬다
    @DisplayName("아웃박스 페이로드는 JSON 으로 저장했다가 읽어도 내용이 같다")
    void payloadRoundTrip() throws Exception {
        ChunkContent.SubjectChunk chunk = (ChunkContent.SubjectChunk)
                ChunkContent.split(converter.toCurriculum(objectMapper.readTree(DOCUMENT))).get(1);
        DeploymentEventPayload payload = DeploymentEventPayload.builder()
                .deploymentId(UUID.randomUUID())
                .targetSystem("ONLINE_CAMPUS")
                .environment("DEV")
                .curriculumId("CUR001")
                .subjectChunk(chunk)
                .chunkOffset(1)
                .build();

        DeploymentEventPayload read = objectMapper.readValue(objectMapper.writeValueAsString(payload), DeploymentEventPayload.class);

        ChunkContent content = read.getContent();
        assertEquals(chunk, assertInstanceOf(ChunkContent.SubjectChunk.class, content));
        assertEquals("ONLINE_CAMPUS", read.getTargetSystem());
    }

}
