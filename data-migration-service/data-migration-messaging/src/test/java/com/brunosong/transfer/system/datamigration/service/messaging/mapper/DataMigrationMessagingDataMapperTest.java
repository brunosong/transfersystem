package com.brunosong.transfer.system.datamigration.service.messaging.mapper;

import com.brunosong.deployment.contract.Applied;
import com.brunosong.deployment.contract.AppliedSkeleton;
import com.brunosong.deployment.contract.AppliedSubject;
import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.contract.ApplyResult;
import com.brunosong.deployment.contract.Grade;
import com.brunosong.deployment.contract.Lesson;
import com.brunosong.deployment.contract.Rejected;
import com.brunosong.deployment.contract.Semester;
import com.brunosong.deployment.contract.SkeletonContent;
import com.brunosong.deployment.contract.Subject;
import com.brunosong.deployment.contract.SubjectContent;
import com.brunosong.deployment.contract.Unit;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.AppliedContent;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.transfer.system.datamigration.service.dto.message.ApplyChunkRequest;
import com.brunosong.transfer.system.datamigration.service.dto.message.ApplyResultMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataMigrationMessagingDataMapperTest {

    private static final String DEPLOYMENT_ID = "6f1d9f5e-7f4b-4c55-9a35-3c2b0f6a1d11";
    private static final Instant NOW = Instant.parse("2026-11-05T00:00:00Z");

    private final DataMigrationMessagingDataMapper mapper = new DataMigrationMessagingDataMapper();

    @Test
    @DisplayName("뼈대 명령을 옮기면 봉투의 값과 학기별 과목 ID 목록이 그대로 남는다")
    void skeletonCommand() {
        Semester semester = Semester.newBuilder().setId("S1").setTitle("1학기").setSubjectIds(List.of("SUB1", "SUB2")).build();
        Grade grade = Grade.newBuilder().setId("G1").setTitle("1학년").setSemesters(List.of(semester)).build();
        SkeletonContent skeleton = SkeletonContent.newBuilder().setTitle("중등").setGrades(List.of(grade)).build();

        ApplyChunkRequest request = mapper.toApplyChunkRequest(command(0, skeleton));

        assertEquals(DEPLOYMENT_ID, request.deploymentId());
        assertEquals(0, request.chunkNo());
        assertEquals(2, request.attempt());
        assertEquals("ONLINE_CAMPUS", request.targetSystem());
        assertEquals("DEV", request.environment());
        assertEquals("CUR001", request.curriculumId());

        ChunkContent.Skeleton content = assertInstanceOf(ChunkContent.Skeleton.class, request.content());
        assertEquals("중등", content.title());
        assertEquals(null, content.description());
        assertEquals(List.of("SUB1", "SUB2"), content.grades().get(0).semesters().get(0).subjectIds());
    }

    @Test
    @DisplayName("과목 명령을 옮기면 차시까지 값이 그대로 남는다")
    void subjectCommand() {
        Lesson lesson = Lesson.newBuilder().setId("L1").setTitle("차시 1").setLessonNumber(1).setLearningLevel(3).build();
        Unit unit = Unit.newBuilder().setId("U1").setTitle("단원 1").setLessons(List.of(lesson)).build();
        Subject subject = Subject.newBuilder().setId("SUB1").setTitle("수학").setDescription("수학 학습").setUnits(List.of(unit)).build();
        SubjectContent subjectContent = SubjectContent.newBuilder().setGradeId("G1").setSemesterId("S1").setSubject(subject).build();

        ApplyChunkRequest request = mapper.toApplyChunkRequest(command(1, subjectContent));

        ChunkContent.SubjectChunk content = assertInstanceOf(ChunkContent.SubjectChunk.class, request.content());
        assertEquals("S1", content.semesterId());
        assertEquals("수학 학습", content.subject().description());
        ChunkContent.Lesson mapped = content.subject().units().get(0).lessons().get(0);
        assertEquals(new ChunkContent.Lesson("L1", "차시 1", null, 1, 3), mapped);
    }

    @Test
    @DisplayName("모르는 내용 타입은 해석할 수 없는 명령이므로 예외로 올린다")
    void unknownContent() {
        ApplyChunkCommand command = command(0, Rejected.newBuilder().setMessage("내용이 아님").build());

        assertThrows(IllegalArgumentException.class, () -> mapper.toApplyChunkRequest(command));
    }

    @Test
    @DisplayName("과목 반영 결과에는 명령의 번호와 저장한 값이 담긴다")
    void appliedSubject() {
        ApplyResult result = mapper.toApplyResult(new ApplyResultMessage(DEPLOYMENT_ID, 3, 2,
                new ApplyResultMessage.Applied(new AppliedContent.Subject("SUB1", "수학", 5, 500)), NOW));

        assertEquals(DEPLOYMENT_ID, result.getDeploymentId());
        assertEquals(3, result.getChunkNo());
        assertEquals(2, result.getAttempt());
        assertEquals(NOW, result.getOccurredAt());

        Applied outcome = assertInstanceOf(Applied.class, result.getOutcome());
        assertEquals(AppliedSubject.newBuilder().setSubjectId("SUB1").setTitle("수학").setUnitCount(5).setLessonCount(500).build(),
                outcome.getContent());
    }

    @Test
    @DisplayName("뼈대 반영 결과에는 학년과 학기 수, 뺀 과목이 담긴다")
    void appliedSkeleton() {
        ApplyResult result = mapper.toApplyResult(new ApplyResultMessage(DEPLOYMENT_ID, 0, 1,
                new ApplyResultMessage.Applied(new AppliedContent.Skeleton(4, 8, List.of())), NOW));

        Applied outcome = assertInstanceOf(Applied.class, result.getOutcome());
        AppliedSkeleton content = assertInstanceOf(AppliedSkeleton.class, outcome.getContent());
        assertEquals(4, content.getGradeCount());
        assertEquals(8, content.getSemesterCount());
        assertEquals(List.of(), content.getRemovedSubjectIds());
    }

    @Test
    @DisplayName("실패 결과에는 운영자가 읽을 사유가 담긴다")
    void rejected() {
        ApplyResult result = mapper.toApplyResult(new ApplyResultMessage(DEPLOYMENT_ID, 3, 1,
                new ApplyResultMessage.Rejected("학기를 찾을 수 없습니다"), NOW));

        Rejected outcome = assertInstanceOf(Rejected.class, result.getOutcome());
        assertEquals("학기를 찾을 수 없습니다", outcome.getMessage());
    }

    private ApplyChunkCommand command(int chunkNo, Object content) {
        return ApplyChunkCommand.newBuilder()
                .setDeploymentId(DEPLOYMENT_ID)
                .setChunkNo(chunkNo)
                .setAttempt(2)
                .setTargetSystem("ONLINE_CAMPUS")
                .setEnvironment("DEV")
                .setCurriculumId("CUR001")
                .setContent(content)
                .setCreatedAt(NOW)
                .build();
    }
}
