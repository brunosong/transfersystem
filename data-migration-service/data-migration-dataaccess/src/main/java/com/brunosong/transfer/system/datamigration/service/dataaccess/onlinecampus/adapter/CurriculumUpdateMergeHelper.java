package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurriculumUpdateMergeHelper {

    public CurriculumEntity curriculumUpdate(CurriculumEntity existingCurriculumEntity, CurriculumEntity newCurriculumEntity) {

        existingCurriculumEntity.setDescription(newCurriculumEntity.getDescription());
        existingCurriculumEntity.setTitle(newCurriculumEntity.getTitle());

        List<GradeEntity> existingGrades = existingCurriculumEntity.getGradeEntities();
        List<GradeEntity> newGrades = newCurriculumEntity.getGradeEntities();

        // 기존 GradeEntity 업데이트
        Map<String, GradeEntity> existingGradeMap = existingGrades.stream()
                .collect(Collectors.toMap(GradeEntity::getGradeId, grade -> grade));

        for (GradeEntity newGrade : newGrades) {
            GradeEntity existingGrade = existingGradeMap.get(newGrade.getGradeId());
            if (existingGrade != null) {
                // 기존 GradeEntity 업데이트
                existingGrade.setTitle(newGrade.getTitle());
                existingGrade.setDescription(newGrade.getDescription());

                mergeSemester(existingGrade, newGrade);
            } else {
                // 새 GradeEntity 추가
                newGrade.setCurriculumEntity(existingCurriculumEntity);
                existingGrades.add(newGrade);
            }
        }

        // 삭제된 GradeEntity 제거
        existingGrades.removeIf(grade -> !newGrades.stream()
                .anyMatch(newGrade -> newGrade.getGradeId().equals(grade.getGradeId())));

        return existingCurriculumEntity;
    }

    public GradeEntity mergeSemester(GradeEntity existingGrade, GradeEntity newGrade) {

        List<SemesterEntity> semesterEntities = existingGrade.getSemesterEntities();
        List<SemesterEntity> newSemesters = newGrade.getSemesterEntities();

        Map<String, SemesterEntity> existingSemesterMap = semesterEntities.stream()
                .collect(Collectors.toMap(SemesterEntity::getSemesterId, semester -> semester));

        for (SemesterEntity newSemester : newSemesters) {
            SemesterEntity existingSemester = existingSemesterMap.get(newSemester.getSemesterId());

            if (existingSemester != null) {
                existingSemester.setTitle(newSemester.getTitle());
                existingSemester.setDescription(newSemester.getDescription());
            } else {
                // 새 GradeEntity 추가
                newSemester.setGradeEntity(existingGrade);
                semesterEntities.add(newSemester);
            }
        }

        semesterEntities.removeIf(semester -> !newSemesters.stream()
                .anyMatch(newSemester -> newSemester.getSemesterId().equals(semester.getSemesterId())));

        return existingGrade;
    }

    public SubjectEntity mergeSubject(SubjectEntity existingSubject, SubjectEntity newSubject) {

        existingSubject.setTitle(newSubject.getTitle());
        existingSubject.setDescription(newSubject.getDescription());

        mergeUnit(existingSubject, newSubject);

        return existingSubject;
    }


    public SemesterEntity mergeSubject(SemesterEntity existingSemester, SemesterEntity newSemester) {

        List<SubjectEntity> existingSubjectEntities = existingSemester.getSubjectEntities();
        List<SubjectEntity> newSubjectEntities = newSemester.getSubjectEntities();

        Map<String, SubjectEntity> existingSubjectMap = existingSubjectEntities.stream()
                .collect(Collectors.toMap(SubjectEntity::getSubjectId, subject -> subject));

        for (SubjectEntity newSubject : newSubjectEntities) {

            SubjectEntity subjectEntity = existingSubjectMap.get(newSubject.getSubjectId());

            if (subjectEntity != null) {
                subjectEntity.setTitle(newSubject.getTitle());
                subjectEntity.setDescription(newSubject.getDescription());

                mergeUnit(subjectEntity, newSubject);

            } else {
                newSubject.setSemesterEntity(existingSemester);
                existingSubjectEntities.add(newSubject);
            }
        }

        return existingSemester;
    }


    public SubjectEntity mergeUnit(SubjectEntity existingSubject, SubjectEntity newSubject) {

        List<UnitEntity> unitEntities = existingSubject.getUnitEntities();
        List<UnitEntity> newUnits = newSubject.getUnitEntities();

        Map<String, UnitEntity> existingUnitMap = unitEntities.stream()
                .collect(Collectors.toMap(UnitEntity::getUnitId, unit -> unit));

        for (UnitEntity newUnit : newUnits) {
            UnitEntity existingUnit = existingUnitMap.get(newUnit.getUnitId());

            if (existingUnit != null) {
                existingUnit.setTitle(newUnit.getTitle());
                existingUnit.setDescription(newUnit.getDescription());

                mergeLesson(existingUnit, newUnit);

            } else {
                newUnit.setSubjectEntity(existingSubject);
                unitEntities.add(newUnit);
            }
        }

        // 기존에 있는 Unit 들을 지울 것인지 지우지 않을 것인지에 대한건 생각을 해봐야 한다.
        unitEntities.removeIf(unit -> !newUnits.stream()
                .anyMatch(newSemester -> newSemester.getUnitId().equals(unit.getUnitId())));

        return existingSubject;
    }

    public UnitEntity mergeLesson(UnitEntity existingUnit, UnitEntity newUnit) {

        List<LessonEntity> existingLessonEntities = existingUnit.getLessonEntities();
        List<LessonEntity> newLessonEntities = newUnit.getLessonEntities();

        Map<String, LessonEntity> existingUnitMap = existingLessonEntities.stream()
                .collect(Collectors.toMap(LessonEntity::getLessonId, lesson -> lesson));

        for (LessonEntity newLesson : newLessonEntities) {

            LessonEntity lessonEntity = existingUnitMap.get(newLesson.getLessonId());

            if (lessonEntity != null) {
                lessonEntity.setTitle(newLesson.getTitle());
                lessonEntity.setDescription(newLesson.getDescription());
            } else {
                newLesson.setUnitEntity(existingUnit);
                existingLessonEntities.add(newLesson);
            }
        }

        // 기존 객체 삭제
        existingLessonEntities.removeIf(lesson -> !newLessonEntities.stream()
                .anyMatch(newLesson -> newLesson.getLessonId().equals(lesson.getLessonId())));

        return existingUnit;
    }
}
