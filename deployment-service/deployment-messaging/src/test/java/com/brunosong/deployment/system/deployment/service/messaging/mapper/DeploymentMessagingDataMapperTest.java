package com.brunosong.deployment.system.deployment.service.messaging.mapper;

import com.brunosong.deployment.contract.Applied;
import com.brunosong.deployment.contract.AppliedSubject;
import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.contract.ApplyResult;
import com.brunosong.deployment.contract.Rejected;
import com.brunosong.deployment.contract.SkeletonContent;
import com.brunosong.deployment.contract.SubjectContent;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeploymentMessagingDataMapperTest {

    private static final UUID DEPLOYMENT_ID = UUID.fromString("6f1d9f5e-7f4b-4c55-9a35-3c2b0f6a1d11");
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.parse("2026-11-05T00:00:00Z");

    private final DeploymentMessagingDataMapper mapper = new DeploymentMessagingDataMapper();

    @Test
    @DisplayName("뼈대 페이로드는 봉투 값과 과목 ID 목록을 담은 명령이 된다")
    void skeleton() {
        ChunkContent.Semester semester = new ChunkContent.Semester("S1", "1학기", null, List.of("SUB1", "SUB2"));
        ChunkContent.Skeleton skeleton = new ChunkContent.Skeleton("중등", "중등 과정",
                List.of(new ChunkContent.Grade("G1", "1학년", null, List.of(semester))));

        ApplyChunkCommand command = mapper.toApplyChunkCommand(payload(0).skeleton(skeleton).build());

        assertEquals(DEPLOYMENT_ID.toString(), command.getDeploymentId());
        assertEquals(0, command.getChunkNo());
        assertEquals(1, command.getAttempt());
        assertEquals("ONLINE_CAMPUS", command.getTargetSystem());
        assertEquals("DEV", command.getEnvironment());
        assertEquals("CUR001", command.getCurriculumId());
        assertEquals(CREATED_AT.toInstant(), command.getCreatedAt());

        SkeletonContent content = assertInstanceOf(SkeletonContent.class, command.getContent());
        assertEquals("중등 과정", content.getDescription());
        assertEquals(List.of("SUB1", "SUB2"), content.getGrades().get(0).getSemesters().get(0).getSubjectIds());
    }

    @Test
    @DisplayName("과목 페이로드는 차시까지 담은 명령이 되고, 단원과 차시의 attributes가 그대로 실린다. 단원 attributes가 없으면 \"{}\"로 나간다")
    void subject() {
        ChunkContent.Lesson lesson = new ChunkContent.Lesson("L1", "차시 1", null, 1, 3, "{\"learningLevel\":3}");
        ChunkContent.Subject subject = new ChunkContent.Subject("SUB1", "수학", null,
                List.of(new ChunkContent.Unit("U1", "단원 1", null, null, List.of(lesson))));
        ChunkContent.SubjectChunk chunk = new ChunkContent.SubjectChunk("G1", "S1", subject);

        ApplyChunkCommand command = mapper.toApplyChunkCommand(payload(1).subjectChunk(chunk).build());

        SubjectContent content = assertInstanceOf(SubjectContent.class, command.getContent());
        assertEquals("S1", content.getSemesterId());
        com.brunosong.deployment.contract.Lesson mapped = content.getSubject().getUnits().get(0).getLessons().get(0);
        assertEquals("L1", mapped.getId());
        assertEquals(1, mapped.getLessonNumber());
        assertEquals(3, mapped.getLearningLevel());
        assertEquals("{\"learningLevel\":3}", mapped.getAttributes());
        assertEquals("{}", content.getSubject().getUnits().get(0).getAttributes());
    }

    @Test
    @DisplayName("성공 결과는 청크 번호와 SUCCESS 로 옮긴다")
    void appliedResult() {
        Applied applied = Applied.newBuilder()
                .setContent(AppliedSubject.newBuilder().setSubjectId("SUB1").setTitle("수학").setUnitCount(5).setLessonCount(500).build())
                .build();

        ApplyResultMessage response = mapper.toApplyResultMessage(result(3, applied));

        assertEquals(DEPLOYMENT_ID, response.getDeploymentId().getValue());
        assertEquals(3, response.getChunkOffset());
        assertEquals(DeploymentStatus.SUCCESS, response.getDeploymentStatus());
    }

    @Test
    @DisplayName("실패 결과는 FAILED 와 대상이 보낸 사유로 옮긴다")
    void rejectedResult() {
        ApplyResultMessage response = mapper.toApplyResultMessage(
                result(3, Rejected.newBuilder().setMessage("학기를 찾을 수 없습니다").build()));

        assertEquals(DeploymentStatus.FAILED, response.getDeploymentStatus());
        assertEquals("학기를 찾을 수 없습니다", response.getMessage());
    }

    @Test
    @DisplayName("모르는 결과 타입은 예외로 올린다")
    void unknownResult() {
        ApplyResult result = result(3, SkeletonContent.newBuilder().setTitle("결과가 아님").setGrades(List.of()).build());

        assertThrows(IllegalArgumentException.class, () -> mapper.toApplyResultMessage(result));
    }

    private ApplyResult result(int chunkNo, Object outcome) {
        return ApplyResult.newBuilder()
                .setDeploymentId(DEPLOYMENT_ID.toString())
                .setChunkNo(chunkNo)
                .setAttempt(1)
                .setOutcome(outcome)
                .setOccurredAt(CREATED_AT.toInstant())
                .build();
    }

    private DeploymentEventPayload.DeploymentEventPayloadBuilder payload(int chunkNo) {
        return DeploymentEventPayload.builder()
                .deploymentId(DEPLOYMENT_ID)
                .targetSystem("ONLINE_CAMPUS")
                .environment("DEV")
                .curriculumId("CUR001")
                .chunkOffset(chunkNo)
                .createdAt(CREATED_AT);
    }
}
