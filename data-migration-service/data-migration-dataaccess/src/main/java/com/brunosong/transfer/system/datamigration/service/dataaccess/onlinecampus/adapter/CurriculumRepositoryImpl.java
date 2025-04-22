package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.mapper.OnlineCampusDataAccessMapper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.SemesterJpaRepository;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.SubjectJpaRepository;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.CurriculumKey;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumJpaRepository curriculumJpaRepository;
    private final SubjectJpaRepository subjectJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final OnlineCampusDataAccessMapper dataAccessMapper;
    private final CurriculumUpdateMergeHelper curriculumUpdateMergeHelper;

    @Override
    @Transactional
    public DataMigrationDto saveAndUpdate(SourceContentData sourceContentData) {

        Document document = Document.parse(StandardCharsets.UTF_8.decode(sourceContentData.getJsonData()).toString());
        Document curriculumDoc = document.get(CurriculumKey.CURRICULUM.getKey(), Document.class);
        if (curriculumDoc != null) {
            return curriculumToLesson(document);
        } else {
            return subjectToLesson(document);
        }
    }

    private DataMigrationDto curriculumToLesson(Document document) {
        CurriculumEntity curriculumEntity = dataAccessMapper.convertToCurriculum(document);

        Optional<CurriculumEntity> existing = curriculumJpaRepository.findByCurriculumId(curriculumEntity.getCurriculumId());

        if (existing.isPresent()) {
            CurriculumEntity saveEntity = curriculumUpdateMergeHelper.curriculumUpdate(existing.get(), curriculumEntity);
            return dataAccessMapper.toDataMigrationDto(curriculumJpaRepository.save(saveEntity));
        } else {
            CurriculumEntity saveEntity = curriculumJpaRepository.save(curriculumEntity);
            return dataAccessMapper.toDataMigrationDto(saveEntity);
        }
    }

    private DataMigrationDto subjectToLesson(Document document) {

        // SemesterEntity 조회
        String semesterId = document.getString("semesterId");
        SemesterEntity semesterEntity = semesterJpaRepository.findBySemesterId(semesterId).orElseThrow();

        // SubjectEntity 저장
        Document subjectDocument = document.get(CurriculumKey.SUBJECT.getKey(), Document.class);
        SubjectEntity subjectEntity = dataAccessMapper.convertToSubject(subjectDocument, semesterEntity);

        Optional<SubjectEntity> existingSubject = subjectJpaRepository.findBySubjectId(subjectDocument.getString("id"));
        SubjectEntity savedSubject;
        if (existingSubject.isPresent()) {
            savedSubject = existingSubject.get();
            savedSubject.setTitle(subjectEntity.getTitle());
            savedSubject.setDescription(subjectEntity.getDescription());
            savedSubject.setSemesterEntity(semesterEntity);
            // UnitEntities 업데이트
            savedSubject.getUnitEntities().clear();
            for (UnitEntity unit : subjectEntity.getUnitEntities()) {
                unit.setSubjectEntity(savedSubject);
                for (LessonEntity lesson : unit.getLessonEntities()) {
                    lesson.setUnitEntity(unit);
                }
                savedSubject.getUnitEntities().add(unit);
            }
        } else {
            savedSubject = subjectEntity;
        }

        // JPA 저장
        savedSubject = subjectJpaRepository.save(savedSubject);
        return dataAccessMapper.toDataMigrationDto(savedSubject);
    }


}
