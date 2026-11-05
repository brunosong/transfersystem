package com.brunosong.transfer.system.transfer.service.messaging.mapper;

import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.contract.Grade;
import com.brunosong.deployment.contract.Lesson;
import com.brunosong.deployment.contract.Semester;
import com.brunosong.deployment.contract.SkeletonContent;
import com.brunosong.deployment.contract.Subject;
import com.brunosong.deployment.contract.SubjectContent;
import com.brunosong.deployment.contract.Unit;
import com.brunosong.transfer.system.transfer.service.valueobject.ChunkContent;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransferMessagingDataMapper {

    private static final int FIRST_ATTEMPT = 1;

    /** 아웃박스에 적혀 있던 페이로드를 발행 모델로 편다. 재전송이 아직 없어 시도는 항상 1이다 */
    public ApplyChunkCommand toApplyChunkCommand(DataTransferEventPayload payload) {
        return ApplyChunkCommand.newBuilder()
                .setDeploymentId(payload.getTransferId().toString())
                .setChunkNo(payload.getChunkOffset())
                .setAttempt(FIRST_ATTEMPT)
                .setTargetSystem(payload.getTargetSystem())
                .setEnvironment(payload.getEnvironment())
                .setCurriculumId(payload.getSourceId())
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
                .build();
    }

    public DataMigrationResponse toDataMigrationResponse(DataMigrationResponseAvroModel dataMigrationResponseAvroModel) {
        return DataMigrationResponse.builder()
                .chunkOffset(dataMigrationResponseAvroModel.getChunkOffset())
                .transferStatus(TransferStatus.valueOf(dataMigrationResponseAvroModel.getTransferStatus().name()))
                .transferId(new TransferId(UUID.fromString(dataMigrationResponseAvroModel.getTransferId())))
                .message(dataMigrationResponseAvroModel.getMessage())
                .build();
    }

}
