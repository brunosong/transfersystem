package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;


import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurriculumUpdateMergeHelperTest extends CurriculumDummy {

    @InjectMocks
    private CurriculumUpdateMergeHelper curriculumUpdateMergeHelper;

    private CurriculumEntity existingCurriculum;
    private CurriculumEntity newCurriculum;

    @BeforeEach
    void setUp() {
        existingCurriculum = createCurriculum();
        newCurriculum = createNewCurriculum();
    }

    @Test
    @DisplayName("Curriculum, Grade, Semester 업데이트 성공")
    void testCurriculumUpdate_UpdateExistingGradeAndSemester() {
        // 실행
        CurriculumEntity updatedCurriculum = curriculumUpdateMergeHelper.curriculumUpdate(existingCurriculum, newCurriculum);

        // 검증: CurriculumEntity
        assertEquals("CUR001", updatedCurriculum.getCurriculumId());
        assertEquals("New Title", updatedCurriculum.getTitle());
        assertEquals("New Description", updatedCurriculum.getDescription());

        // 검증: GradeEntity
        List<GradeEntity> updatedGrades = updatedCurriculum.getGradeEntities();
        assertEquals(2, updatedGrades.size());

        GradeEntity updatedGrade1 = updatedGrades.stream()
                .filter(g -> g.getGradeId().equals("GRADE001"))
                .findFirst()
                .orElseThrow();
        assertEquals("Updated Grade 1", updatedGrade1.getTitle());
        assertEquals("Updated Grade 1 Desc", updatedGrade1.getDescription());
        assertEquals(existingCurriculum, updatedGrade1.getCurriculumEntity());

        GradeEntity newGrade2 = updatedGrades.stream()
                .filter(g -> g.getGradeId().equals("GRADE002"))
                .findFirst()
                .orElseThrow();
        assertEquals("New Grade 2", newGrade2.getTitle());
        assertEquals("New Grade 2 Desc", newGrade2.getDescription());
        assertEquals(existingCurriculum, newGrade2.getCurriculumEntity());

        // 검증: SemesterEntity
        List<SemesterEntity> updatedSemesters = updatedGrade1.getSemesterEntities();
        assertEquals(2, updatedSemesters.size());

        SemesterEntity updatedSemester1 = updatedSemesters.stream()
                .filter(s -> s.getSemesterId().equals("SEM001"))
                .findFirst()
                .orElseThrow();
        assertEquals("Updated Semester 1", updatedSemester1.getTitle());
        assertEquals("Updated Semester 1 Desc", updatedSemester1.getDescription());
        assertEquals(updatedGrade1, updatedSemester1.getGradeEntity());

        SemesterEntity newSemester2 = updatedSemesters.stream()
                .filter(s -> s.getSemesterId().equals("SEM002"))
                .findFirst()
                .orElseThrow();
        assertEquals("New Semester 2", newSemester2.getTitle());
        assertEquals("New Semester 2 Desc", newSemester2.getDescription());
        assertEquals(updatedGrade1, newSemester2.getGradeEntity());
    }

    @Test
    void testCurriculumUpdate_RemoveGradeAndSemester() {
        // 새 CurriculumEntity에 GRADE001 제외
        newCurriculum.setGradeEntities(new ArrayList<>());

        // 실행
        CurriculumEntity updatedCurriculum = curriculumUpdateMergeHelper.curriculumUpdate(existingCurriculum, newCurriculum);

        // 검증
        assertEquals("New Title", updatedCurriculum.getTitle());
        assertEquals("New Description", updatedCurriculum.getDescription());
        assertTrue(updatedCurriculum.getGradeEntities().isEmpty());
    }

    @Test
    void mergeSubject() {

        SemesterEntity existingSemester = createSemester();
        SemesterEntity newSemester = createNewSemester();

        // 실행
        SemesterEntity updateResult = curriculumUpdateMergeHelper.mergeSubject(existingSemester, newSemester);

        // 검증
        SubjectEntity subjectUpdate = updateResult.getSubjectEntities().get(0);
        assertEquals("SUB001", subjectUpdate.getSubjectId());
        assertEquals("New Subject", subjectUpdate.getTitle());
        assertEquals("New Description", subjectUpdate.getDescription());

        UnitEntity unitEntity = subjectUpdate.getUnitEntities().get(0);
        assertEquals("UNIT001", unitEntity.getUnitId());
        assertEquals("New Unit", unitEntity.getTitle());
        assertEquals("New Unit Desc", unitEntity.getDescription());

        List<LessonEntity> lessonEntities = unitEntity.getLessonEntities();
        assertEquals(2, lessonEntities.size());

        LessonEntity newLesson2 = new LessonEntity();
        newLesson2.setLessonId("LESS002");
        newLesson2.setTitle("New Lesson2");

        LessonEntity lessonEntity1 = lessonEntities.get(0);
        assertEquals("LESSON001", lessonEntity1.getLessonId());
        assertEquals("New Lesson1", lessonEntity1.getTitle());
        assertEquals(unitEntity, lessonEntity1.getUnitEntity());

        LessonEntity lessonEntity2 = lessonEntities.get(1);
        assertEquals("LESSON002", lessonEntity2.getLessonId());
        assertEquals("New Lesson2", lessonEntity2.getTitle());
        assertEquals(unitEntity, lessonEntity2.getUnitEntity());
    }

    @Test
    void testMergeSemester_RemoveSemester() {
        // 기존 GradeEntity
        GradeEntity existingGrade = new GradeEntity();
        existingGrade.setGradeId("GRADE001");
        SemesterEntity semester1 = new SemesterEntity();
        semester1.setSemesterId("SEM001");
        semester1.setTitle("Old Semester");
        semester1.setGradeEntity(existingGrade);
        existingGrade.setSemesterEntities(new ArrayList<>(List.of(semester1)));

        // 새 GradeEntity (Semester 없음)
        GradeEntity newGrade = new GradeEntity();
        newGrade.setGradeId("GRADE001");
        newGrade.setSemesterEntities(new ArrayList<>());

        // 실행
        GradeEntity updatedGrade = curriculumUpdateMergeHelper.mergeSemester(existingGrade, newGrade);

        // 검증
        assertTrue(updatedGrade.getSemesterEntities().isEmpty());
    }
}