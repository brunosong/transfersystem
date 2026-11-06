package com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.mapper;

import com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.deployment.system.datamigration.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.datamigration.service.dto.message.DataMigrationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OnlineCampusDataAccessMapper {

    public DataMigrationDto toDataMigrationDto(CurriculumEntity saveEntity) {
        List<GradeEntity> grades = saveEntity.getGradeEntities();
        int semesterCount = grades.stream().mapToInt(grade -> grade.getSemesterEntities().size()).sum();
        return DataMigrationDto.builder()
                .id(saveEntity.getId())
                .curriculumId(saveEntity.getCurriculumId())
                .applied(new AppliedContent.Skeleton(grades.size(), semesterCount, List.of()))
                .build();
    }

    public DataMigrationDto toDataMigrationDto(SubjectEntity saveEntity) {
        List<UnitEntity> units = saveEntity.getUnitEntities();
        int lessonCount = units.stream().mapToInt(unit -> unit.getLessonEntities().size()).sum();
        return DataMigrationDto.builder()
                .id(saveEntity.getId())
                .curriculumId(saveEntity.getSubjectId())
                .applied(new AppliedContent.Subject(saveEntity.getSubjectId(), saveEntity.getTitle(), units.size(), lessonCount))
                .build();
    }

    public CurriculumEntity toCurriculum(String curriculumId, ChunkContent.Skeleton skeleton) {
        CurriculumEntity curriculumEntity = new CurriculumEntity();
        curriculumEntity.setCurriculumId(curriculumId);
        curriculumEntity.setTitle(skeleton.title());
        curriculumEntity.setDescription(skeleton.description());

        List<GradeEntity> gradeEntities = new ArrayList<>();
        for (ChunkContent.Grade grade : skeleton.grades()) {
            gradeEntities.add(convertToGrade(grade, curriculumEntity));
        }
        curriculumEntity.setGradeEntities(gradeEntities);
        return curriculumEntity;
    }

    private GradeEntity convertToGrade(ChunkContent.Grade grade, CurriculumEntity curriculumEntity) {
        GradeEntity gradeEntity = new GradeEntity();
        gradeEntity.setGradeId(grade.id());
        gradeEntity.setTitle(grade.title());
        gradeEntity.setDescription(grade.description());
        gradeEntity.setCurriculumEntity(curriculumEntity);

        List<SemesterEntity> semesterEntities = new ArrayList<>();
        for (ChunkContent.Semester semester : grade.semesters()) {
            semesterEntities.add(convertToSemester(semester, gradeEntity));
        }
        gradeEntity.setSemesterEntities(semesterEntities);
        return gradeEntity;
    }

    // 과목은 과목 청크가 넣는다. 뼈대는 학기까지만 만든다
    private SemesterEntity convertToSemester(ChunkContent.Semester semester, GradeEntity gradeEntity) {
        SemesterEntity semesterEntity = SemesterEntity.builder()
                .semesterId(semester.id())
                .title(semester.title())
                .description(semester.description())
                .gradeEntity(gradeEntity)
                .build();
        semesterEntity.setSubjectEntities(new ArrayList<>());
        return semesterEntity;
    }

    public SubjectEntity convertToSubject(ChunkContent.Subject subject, SemesterEntity semesterEntity) {
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.setSubjectId(subject.id());
        subjectEntity.setTitle(subject.title());
        subjectEntity.setDescription(subject.description());
        subjectEntity.setSemesterEntity(semesterEntity);

        List<UnitEntity> unitEntities = new ArrayList<>();
        for (ChunkContent.Unit unit : subject.units()) {
            unitEntities.add(convertToUnit(unit, subjectEntity));
        }
        subjectEntity.setUnitEntities(unitEntities);
        return subjectEntity;
    }

    private UnitEntity convertToUnit(ChunkContent.Unit unit, SubjectEntity subjectEntity) {
        UnitEntity unitEntity = new UnitEntity();
        unitEntity.setUnitId(unit.id());
        unitEntity.setTitle(unit.title());
        unitEntity.setDescription(unit.description());
        unitEntity.setSubjectEntity(subjectEntity);

        List<LessonEntity> lessonEntities = new ArrayList<>();
        for (ChunkContent.Lesson lesson : unit.lessons()) {
            lessonEntities.add(convertToLesson(lesson, unitEntity));
        }
        unitEntity.setLessonEntities(lessonEntities);
        return unitEntity;
    }

    private LessonEntity convertToLesson(ChunkContent.Lesson lesson, UnitEntity unitEntity) {
        LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setLessonId(lesson.id());
        lessonEntity.setTitle(lesson.title());
        lessonEntity.setDescription(lesson.description());
        lessonEntity.setLessonNumber(lesson.lessonNumber());
        lessonEntity.setLearningLevel(lesson.learningLevel());
        lessonEntity.setUnitEntity(unitEntity);
        return lessonEntity;
    }

    public void updateSubjectEntityMapping(SubjectEntity savedSubject, SubjectEntity newSubject) {
        savedSubject.setTitle(newSubject.getTitle());
        savedSubject.setDescription(newSubject.getDescription());
        savedSubject.setSemesterEntity(newSubject.getSemesterEntity());
        // UnitEntities 업데이트
        savedSubject.getUnitEntities().clear();
//        for (UnitEntity unit : savedSubject.getUnitEntities()) {
//            updateUnitEntityMapping(unit,);
//
//            savedSubject.getUnitEntities().add(unit);
//        }
    }

    public void updateUnitEntityMapping(UnitEntity savedUnit, UnitEntity newUnit) {
        savedUnit.setDescription(newUnit.getDescription());
        savedUnit.setTitle(newUnit.getTitle());
        savedUnit.setSubjectEntity(newUnit.getSubjectEntity());

//
//        for (LessonEntity lesson : unit.getLessonEntities()) {
//            lesson.setUnitEntity(unit);
//        }
    }

    public void updateLessonEntityMapping(LessonEntity lesson, LessonEntity newLesson) {
        lesson.setTitle(newLesson.getTitle());
        lesson.setDescription(newLesson.getDescription());
        lesson.setLessonNumber(newLesson.getLessonNumber());
        lesson.setLearningLevel(newLesson.getLearningLevel());
        lesson.setUnitEntity(newLesson.getUnitEntity());
    }
}
