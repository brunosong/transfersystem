package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.mapper;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.CurriculumKey;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class OnlineCampusDataAccessMapper {

    public DataMigrationDto toDataMigrationDto(CurriculumEntity saveEntity) {
        return DataMigrationDto.builder()
                .id(saveEntity.getId())
                .curriculumId(saveEntity.getCurriculumId())
                .build();
    }

    public DataMigrationDto toDataMigrationDto(SubjectEntity saveEntity) {
        return DataMigrationDto.builder()
                .id(saveEntity.getId())
                .curriculumId(saveEntity.getSubjectId())
                .build();
    }

    public CurriculumEntity convertToCurriculum(Document document) {

        Document curriculumDoc = document.get(CurriculumKey.CURRICULUM.getKey(), Document.class);

        CurriculumEntity curriculumEntity = new CurriculumEntity();
        curriculumEntity.setCurriculumId(curriculumDoc.getString("curriculumId"));
        curriculumEntity.setTitle(curriculumDoc.getString("title"));
        curriculumEntity.setDescription(curriculumDoc.getString("description"));

        List<Document> gradeDocs = curriculumDoc.getList(CurriculumKey.GRADE.getKey(), Document.class, Collections.emptyList());
        if (!gradeDocs.isEmpty()) {
            List<GradeEntity> gradeEntities = new ArrayList<>();
            for (Document gradeDoc : gradeDocs) {
                gradeEntities.add(convertToGrade(gradeDoc, curriculumEntity));
            }
            curriculumEntity.setGradeEntities(gradeEntities);
        }
        return curriculumEntity;
    }

    private GradeEntity convertToGrade(Document gradeDoc, CurriculumEntity curriculumEntity) {
        GradeEntity gradeEntity = new GradeEntity();
        gradeEntity.setGradeId(gradeDoc.getString("id"));
        gradeEntity.setTitle(gradeDoc.getString("title"));
        gradeEntity.setDescription(gradeDoc.getString("description"));
        gradeEntity.setCurriculumEntity(curriculumEntity);

        List<Document> semesterDocs = gradeDoc.getList(CurriculumKey.SEMESTER.getKey(), Document.class, Collections.emptyList());
        if (!semesterDocs.isEmpty()) {
            List<SemesterEntity> semesterEntities = new ArrayList<>();
            for (Document semesterDoc : semesterDocs) {
                semesterEntities.add(convertToSemester(semesterDoc, gradeEntity));
            }
            gradeEntity.setSemesterEntities(semesterEntities);
        }
        return gradeEntity;
    }

    private SemesterEntity convertToSemester(Document semesterDoc, GradeEntity gradeEntity) {
        SemesterEntity semesterEntity = SemesterEntity.builder()
                .semesterId(semesterDoc.getString("id"))
                .title(semesterDoc.getString("title"))
                .description(semesterDoc.getString("description"))
                .gradeEntity(gradeEntity)
                .build();

        List<Document> subjectDocs = semesterDoc.getList(CurriculumKey.SUBJECT.getKey(), Document.class, Collections.emptyList());
        List<SubjectEntity> subjectEntities = new ArrayList<>();

        for (Document subjectDoc : subjectDocs) {
            subjectEntities.add(convertToSubject(subjectDoc, semesterEntity));
        }
        semesterEntity.setSubjectEntities(subjectEntities);

        return semesterEntity;
    }

    public SubjectEntity convertToSubject(Document subjectDoc, SemesterEntity semesterEntity) {
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.setSubjectId(subjectDoc.getString("id"));
        subjectEntity.setTitle(subjectDoc.getString("title"));
        subjectEntity.setDescription(subjectDoc.getString("description"));
        subjectEntity.setSemesterEntity(semesterEntity);

        List<Document> unitDocs = subjectDoc.getList(CurriculumKey.UNIT.getKey(), Document.class, Collections.emptyList());
        if (!unitDocs.isEmpty()) {
            List<UnitEntity> unitEntities = new ArrayList<>();
            for (Document unitDoc : unitDocs) {
                unitEntities.add(convertToUnit(unitDoc, subjectEntity));
            }
            subjectEntity.setUnitEntities(unitEntities);
        }
        return subjectEntity;
    }

    private UnitEntity convertToUnit(Document unitDoc, SubjectEntity subjectEntity) {
        UnitEntity unitEntity = new UnitEntity();
        unitEntity.setUnitId(unitDoc.getString("id"));
        unitEntity.setTitle(unitDoc.getString("title"));
        unitEntity.setDescription(unitDoc.getString("description"));
        unitEntity.setSubjectEntity(subjectEntity);

        List<Document> lessonDocs = unitDoc.getList(CurriculumKey.LESSON.getKey(), Document.class, Collections.emptyList());
        if (!lessonDocs.isEmpty()) {
            List<LessonEntity> lessonEntities = new ArrayList<>();
            for (Document lessonDoc : lessonDocs) {
                lessonEntities.add(convertToLesson(lessonDoc, unitEntity));
            }
            unitEntity.setLessonEntities(lessonEntities);
        }
        return unitEntity;
    }

    private LessonEntity convertToLesson(Document lessonDoc, UnitEntity unitEntity) {
        LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setLessonId(lessonDoc.getString("id"));
        lessonEntity.setTitle(lessonDoc.getString("title"));
        lessonEntity.setDescription(lessonDoc.getString("description"));
        lessonEntity.setLessonNumber(lessonDoc.getInteger("lessonNumber"));
        lessonEntity.setLearningLevel(lessonDoc.getInteger("learningLevel"));
        lessonEntity.setUnitEntity(unitEntity);

        return lessonEntity;
    }
}
