package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CurriculumDummy {


    protected CurriculumEntity createCurriculum() {

        CurriculumEntity curriculum = new CurriculumEntity();
        curriculum.setCurriculumId("CUR001");
        curriculum.setTitle("Old Title");
        curriculum.setDescription("Old Description");

        GradeEntity grade1 = new GradeEntity();
        grade1.setGradeId("GRADE001");
        grade1.setTitle("Old Grade 1");
        grade1.setDescription("Old Grade 1 Desc");
        grade1.setCurriculumEntity(curriculum);

        SemesterEntity semester1 = new SemesterEntity();
        semester1.setSemesterId("SEM001");
        semester1.setTitle("Old Semester 1");
        semester1.setDescription("Old Semester 1 Desc");
        semester1.setGradeEntity(grade1);

        grade1.setSemesterEntities(new ArrayList<>(List.of(semester1)));
        curriculum.setGradeEntities(new ArrayList<>(List.of(grade1)));

        return curriculum;
    }

    protected CurriculumEntity createNewCurriculum() {
        // 새 CurriculumEntity 설정
        CurriculumEntity newCurriculum = new CurriculumEntity();
        newCurriculum.setCurriculumId("CUR001");
        newCurriculum.setTitle("New Title");
        newCurriculum.setDescription("New Description");

        GradeEntity newGrade1 = new GradeEntity();
        newGrade1.setGradeId("GRADE001");
        newGrade1.setTitle("Updated Grade 1");
        newGrade1.setDescription("Updated Grade 1 Desc");

        GradeEntity newGrade2 = new GradeEntity();
        newGrade2.setGradeId("GRADE002");
        newGrade2.setTitle("New Grade 2");
        newGrade2.setDescription("New Grade 2 Desc");

        SemesterEntity newSemester1 = new SemesterEntity();
        newSemester1.setSemesterId("SEM001");
        newSemester1.setTitle("Updated Semester 1");
        newSemester1.setDescription("Updated Semester 1 Desc");

        SemesterEntity newSemester2 = new SemesterEntity();
        newSemester2.setSemesterId("SEM002");
        newSemester2.setTitle("New Semester 2");
        newSemester2.setDescription("New Semester 2 Desc");

        newGrade1.setSemesterEntities(new ArrayList<>(Arrays.asList(newSemester1, newSemester2)));
        newGrade2.setSemesterEntities(new ArrayList<>());
        newCurriculum.setGradeEntities(new ArrayList<>(Arrays.asList(newGrade1, newGrade2)));

        return newCurriculum;
    }

    protected SemesterEntity createSemester() {

        SemesterEntity semester = new SemesterEntity();
        semester.setSemesterId("SEM001");
        semester.setTitle("Old Semester 1");
        semester.setDescription("Old Semester 1 Desc");

        SubjectEntity existingSubject = new SubjectEntity();
        existingSubject.setSubjectId("SUB001");
        existingSubject.setTitle("Old Subject");
        existingSubject.setDescription("Old Description");
        existingSubject.setSemesterEntity(semester);

        UnitEntity existingUnit = new UnitEntity();
        existingUnit.setUnitId("UNIT001");
        existingUnit.setTitle("Old Unit");
        existingUnit.setSubjectEntity(existingSubject);

        LessonEntity existingLesson = new LessonEntity();
        existingLesson.setLessonId("LESSON001");
        existingLesson.setTitle("Old Lesson");
        existingLesson.setUnitEntity(existingUnit);

        LessonEntity existingLesson3 = new LessonEntity();
        existingLesson3.setLessonId("LESSON003");
        existingLesson3.setTitle("Old Lesson");
        existingLesson3.setUnitEntity(existingUnit);

        semester.setSubjectEntities(new ArrayList<>(List.of(existingSubject)));
        existingUnit.setLessonEntities(new ArrayList<>(List.of(existingLesson,existingLesson3)));
        existingSubject.setUnitEntities(new ArrayList<>(List.of(existingUnit)));

        return semester;
    }


    protected SemesterEntity createNewSemester() {

        SemesterEntity newSemester = new SemesterEntity();
        newSemester.setSemesterId("SEM001");
        newSemester.setTitle("Updated Semester 1");
        newSemester.setDescription("Updated Semester 1 Desc");

        // Given: 기존 SubjectEntity 저장
        SubjectEntity newSubject = new SubjectEntity();
        newSubject.setSubjectId("SUB001");
        newSubject.setTitle("New Subject");
        newSubject.setDescription("New Description");
        newSubject.setSemesterEntity(newSemester);

        UnitEntity newUnit = new UnitEntity();
        newUnit.setUnitId("UNIT001");
        newUnit.setTitle("New Unit");
        newUnit.setDescription("New Unit Desc");
        newUnit.setSubjectEntity(newSubject);

        LessonEntity newLesson1 = new LessonEntity();
        newLesson1.setLessonId("LESSON001");
        newLesson1.setTitle("New Lesson1");
        newLesson1.setUnitEntity(newUnit);

        LessonEntity newLesson2 = new LessonEntity();
        newLesson2.setLessonId("LESSON002");
        newLesson2.setTitle("New Lesson2");
        newLesson2.setUnitEntity(newUnit);

        newSemester.setSubjectEntities(new ArrayList<>(List.of(newSubject)));
        newUnit.setLessonEntities(new ArrayList<>(List.of(newLesson1,newLesson2)));
        newSubject.setUnitEntities(new ArrayList<>(List.of(newUnit)));

        return newSemester;
    }

    protected SemesterEntity createNewSemester03() {

        SemesterEntity newSemester = new SemesterEntity();
        newSemester.setSemesterId("SEM003");
        newSemester.setTitle("Updated Semester 3");
        newSemester.setDescription("Updated Semester 3 Desc");

        return newSemester;
    }
}
