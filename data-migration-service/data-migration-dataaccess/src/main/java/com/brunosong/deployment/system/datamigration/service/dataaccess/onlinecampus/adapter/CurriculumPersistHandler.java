package com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.entity.CurriculumEntity;
import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.entity.SemesterEntity;
import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.entity.SubjectEntity;
import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.mapper.OnlineCampusDataAccessMapper;
import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.repository.SemesterJpaRepository;
import com.brunosong.deployment.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.datamigration.service.dto.message.DataMigrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurriculumPersistHandler {

    private final CurriculumJpaRepository curriculumJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final OnlineCampusDataAccessMapper dataAccessMapper;
    private final CurriculumUpdateMergeHelper curriculumUpdateMergeHelper;

    public DataMigrationDto curriculumToLesson(String curriculumId, ChunkContent.Skeleton skeleton) {
        CurriculumEntity curriculumEntity = dataAccessMapper.toCurriculum(curriculumId, skeleton);

        Optional<CurriculumEntity> existing = curriculumJpaRepository.findByCurriculumId(curriculumEntity.getCurriculumId());

        if (existing.isPresent()) {
            CurriculumEntity saveEntity = curriculumUpdateMergeHelper.curriculumUpdate(existing.get(), curriculumEntity);
            return dataAccessMapper.toDataMigrationDto(curriculumJpaRepository.save(saveEntity));
        } else {
            CurriculumEntity saveEntity = curriculumJpaRepository.save(curriculumEntity);
            return dataAccessMapper.toDataMigrationDto(saveEntity);
        }
    }

    public DataMigrationDto subjectToLesson(ChunkContent.SubjectChunk chunk) {

        // SemesterEntity 조회
        SemesterEntity semesterEntity = semesterJpaRepository.findBySemesterId(chunk.semesterId()).orElseThrow();

        // SubjectEntity 저장
        SubjectEntity newSubject = dataAccessMapper.convertToSubject(chunk.subject(), semesterEntity);
        
        // 기존 Subject가 존재하는지 확인
        Optional<SubjectEntity> existingSubject = semesterEntity.getSubjectEntities().stream().filter(subject ->
            subject.getSubjectId().equals(newSubject.getSubjectId())
        ).findFirst();

        if (existingSubject.isPresent()) {
            curriculumUpdateMergeHelper.mergeSubject(existingSubject.get(), newSubject);
        } else {
            semesterEntity.addSubjectEntity(newSubject);
        }

        // JPA 저장
        SemesterEntity result = semesterJpaRepository.saveAndFlush(semesterEntity);
        return dataAccessMapper.toDataMigrationDto(result.getSubjectEntities().stream()
                .filter(subject -> subject.getSubjectId().equals(newSubject.getSubjectId())).findFirst().get());
    }

}
