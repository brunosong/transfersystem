package com.brunosong.deployment.system.targetagent.service.messaging.mapper;

import com.brunosong.deployment.contract.Applied;
import com.brunosong.deployment.contract.AppliedSkeleton;
import com.brunosong.deployment.contract.AppliedSubject;
import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.contract.ApplyResult;
import com.brunosong.deployment.contract.Rejected;
import com.brunosong.deployment.contract.SkeletonContent;
import com.brunosong.deployment.contract.SubjectContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyChunkRequest;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyResultMessage;
import org.springframework.stereotype.Component;

/**
 * 메시지 계약과 안쪽 계층의 모델을 서로 옮긴다. Avro 클래스를 아는 곳은 messaging 모듈뿐이다.
 */
@Component
public class TargetAgentMessagingDataMapper {

    public ApplyChunkRequest toApplyChunkRequest(ApplyChunkCommand command) {
        return new ApplyChunkRequest(
                command.getDeploymentId(),
                command.getChunkNo(),
                command.getAttempt(),
                command.getTargetSystem(),
                command.getEnvironment(),
                command.getCurriculumId(),
                toChunkContent(command.getContent()));
    }

    public ApplyResult toApplyResult(ApplyResultMessage message) {
        return ApplyResult.newBuilder()
                .setDeploymentId(message.deploymentId())
                .setChunkNo(message.chunkNo())
                .setAttempt(message.attempt())
                .setOutcome(toOutcome(message.outcome()))
                .setOccurredAt(message.occurredAt())
                .build();
    }

    // 유니온은 생성 코드에서 Object 로 나온다. 모르는 타입은 해석할 수 없는 명령이므로 예외로 올려 DLT 로 보낸다
    private ChunkContent toChunkContent(Object content) {
        return switch (content) {
            case SkeletonContent skeleton -> new ChunkContent.Skeleton(
                    skeleton.getTitle(),
                    skeleton.getDescription(),
                    skeleton.getGrades().stream().map(this::toGrade).toList());
            case SubjectContent subject -> new ChunkContent.SubjectChunk(
                    subject.getGradeId(),
                    subject.getSemesterId(),
                    toSubject(subject.getSubject()));
            default -> throw new IllegalArgumentException("모르는 내용 타입입니다: " + content.getClass().getName());
        };
    }

    private ChunkContent.Grade toGrade(com.brunosong.deployment.contract.Grade grade) {
        return new ChunkContent.Grade(grade.getId(), grade.getTitle(), grade.getDescription(),
                grade.getSemesters().stream().map(this::toSemester).toList());
    }

    private ChunkContent.Semester toSemester(com.brunosong.deployment.contract.Semester semester) {
        return new ChunkContent.Semester(semester.getId(), semester.getTitle(), semester.getDescription(),
                semester.getSubjectIds());
    }

    private ChunkContent.Subject toSubject(com.brunosong.deployment.contract.Subject subject) {
        return new ChunkContent.Subject(subject.getId(), subject.getTitle(), subject.getDescription(),
                subject.getUnits().stream().map(this::toUnit).toList());
    }

    private ChunkContent.Unit toUnit(com.brunosong.deployment.contract.Unit unit) {
        return new ChunkContent.Unit(unit.getId(), unit.getTitle(), unit.getDescription(),
                unit.getLessons().stream().map(this::toLesson).toList());
    }

    private ChunkContent.Lesson toLesson(com.brunosong.deployment.contract.Lesson lesson) {
        return new ChunkContent.Lesson(lesson.getId(), lesson.getTitle(), lesson.getDescription(),
                lesson.getLessonNumber(), lesson.getLearningLevel());
    }

    private Object toOutcome(ApplyResultMessage.Outcome outcome) {
        return switch (outcome) {
            case ApplyResultMessage.Applied applied -> Applied.newBuilder()
                    .setContent(toAppliedContent(applied.content()))
                    .build();
            case ApplyResultMessage.Rejected rejected -> Rejected.newBuilder()
                    .setMessage(rejected.message())
                    .build();
        };
    }

    private Object toAppliedContent(AppliedContent content) {
        return switch (content) {
            case AppliedContent.Skeleton skeleton -> AppliedSkeleton.newBuilder()
                    .setGradeCount(skeleton.gradeCount())
                    .setSemesterCount(skeleton.semesterCount())
                    .setRemovedSubjectIds(skeleton.removedSubjectIds())
                    .build();
            case AppliedContent.Subject subject -> AppliedSubject.newBuilder()
                    .setSubjectId(subject.subjectId())
                    .setTitle(subject.title())
                    .setUnitCount(subject.unitCount())
                    .setLessonCount(subject.lessonCount())
                    .build();
        };
    }
}
