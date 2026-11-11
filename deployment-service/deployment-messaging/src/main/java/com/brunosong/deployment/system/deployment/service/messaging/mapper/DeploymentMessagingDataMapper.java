package com.brunosong.deployment.system.deployment.service.messaging.mapper;

import com.brunosong.deployment.contract.Applied;
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
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeploymentMessagingDataMapper {

    private static final int FIRST_ATTEMPT = 1;

    /** 아웃박스에 적혀 있던 페이로드를 발행 모델로 편다. 재전송이 아직 없어 시도는 항상 1이다 */
    public ApplyChunkCommand toApplyChunkCommand(DeploymentEventPayload payload) {
        return ApplyChunkCommand.newBuilder()
                .setDeploymentId(payload.getDeploymentId().toString())
                .setChunkNo(payload.getChunkOffset())
                .setAttempt(FIRST_ATTEMPT)
                .setTargetSystem(payload.getTargetSystem())
                .setEnvironment(payload.getEnvironment())
                .setCurriculumId(payload.getCurriculumId())
                .setContent(toContent(payload.getContent()))
                .setCreatedAt(payload.getCreatedAt().toInstant())
                .build();
    }

    private Object toContent(ChunkContent content) {
        return switch (content) {
            case ChunkContent.Skeleton skeleton -> SkeletonContent.newBuilder()
                    .setTitle(skeleton.title())
                    .setDescription(skeleton.description())
                    .setGrades(skeleton.grades().stream().map(this::toGrade).toList())
                    .build();
            case ChunkContent.SubjectChunk chunk -> SubjectContent.newBuilder()
                    .setGradeId(chunk.gradeId())
                    .setSemesterId(chunk.semesterId())
                    .setSubject(toSubject(chunk.subject()))
                    .build();
        };
    }

    private Grade toGrade(ChunkContent.Grade grade) {
        return Grade.newBuilder()
                .setId(grade.id())
                .setTitle(grade.title())
                .setDescription(grade.description())
                .setSemesters(grade.semesters().stream().map(this::toSemester).toList())
                .build();
    }

    private Semester toSemester(ChunkContent.Semester semester) {
        return Semester.newBuilder()
                .setId(semester.id())
                .setTitle(semester.title())
                .setDescription(semester.description())
                .setSubjectIds(semester.subjectIds())
                .build();
    }

    private Subject toSubject(ChunkContent.Subject subject) {
        return Subject.newBuilder()
                .setId(subject.id())
                .setTitle(subject.title())
                .setDescription(subject.description())
                .setUnits(subject.units().stream().map(this::toUnit).toList())
                .build();
    }

    private Unit toUnit(ChunkContent.Unit unit) {
        return Unit.newBuilder()
                .setId(unit.id())
                .setTitle(unit.title())
                .setDescription(unit.description())
                .setAttributes(unit.attributes())
                .setLessons(unit.lessons().stream().map(this::toLesson).toList())
                .build();
    }

    private Lesson toLesson(ChunkContent.Lesson lesson) {
        return Lesson.newBuilder()
                .setId(lesson.id())
                .setTitle(lesson.title())
                .setDescription(lesson.description())
                .setLessonNumber(lesson.lessonNumber())
                .setLearningLevel(lesson.learningLevel())
                .setAttributes(lesson.attributes())
                .build();
    }

    /**
     * 결과를 지금 처리 로직이 쓰는 모양으로 옮긴다. 상태를 정하는 일은 Deployment 모델이 생기면 그쪽으로 간다.
     * 반영 내용은 아직 저장할 곳이 없어 쓰지 않는다.
     */
    public ApplyResultMessage toApplyResultMessage(ApplyResult result) {
        ApplyResultMessage.ApplyResultMessageBuilder response = ApplyResultMessage.builder()
                .chunkOffset(result.getChunkNo())
                .deploymentId(new DeploymentId(UUID.fromString(result.getDeploymentId())));

        // 유니온은 생성 코드에서 Object 로 나온다. 모르는 타입은 예외로 올려 DLT 로 보낸다
        return switch (result.getOutcome()) {
            case Applied applied -> response
                    .deploymentStatus(DeploymentStatus.SUCCESS)
                    .message("SUCCESS")
                    .build();
            case Rejected rejected -> response
                    .deploymentStatus(DeploymentStatus.FAILED)
                    .message(rejected.getMessage())
                    .build();
            default -> throw new IllegalArgumentException("모르는 결과 타입입니다: " + result.getOutcome().getClass().getName());
        };
    }
}
