package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;


import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.CurriculumEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.GradeEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.SemesterEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurriculumUpdateMergeHelperTest {

    @InjectMocks
    private CurriculumUpdateMergeHelper curriculumUpdateMergeHelper;

    private CurriculumEntity existingCurriculum;
    private CurriculumEntity newCurriculum;

    @BeforeEach
    void setUp() {
        // 기존 CurriculumEntity 설정
        existingCurriculum = new CurriculumEntity();
        existingCurriculum.setCurriculumId("CUR001");
        existingCurriculum.setTitle("Old Title");
        existingCurriculum.setDescription("Old Description");

        GradeEntity grade1 = new GradeEntity();
        grade1.setGradeId("GRADE001");
        grade1.setTitle("Old Grade 1");
        grade1.setDescription("Old Grade 1 Desc");
        grade1.setCurriculumEntity(existingCurriculum);

        SemesterEntity semester1 = new SemesterEntity();
        semester1.setSemesterId("SEM001");
        semester1.setTitle("Old Semester 1");
        semester1.setDescription("Old Semester 1 Desc");
        semester1.setGradeEntity(grade1);

        grade1.setSemesterEntities(new ArrayList<>(List.of(semester1)));
        existingCurriculum.setGradeEntities(new ArrayList<>(List.of(grade1)));

        // 새 CurriculumEntity 설정
        newCurriculum = new CurriculumEntity();
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
    }

    @Test
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
    void testMergeSemester_UpdateAndAddSemester() {
        // 기존 GradeEntity와 새 GradeEntity 준비
        GradeEntity existingGrade = new GradeEntity();
        existingGrade.setGradeId("GRADE001");
        existingGrade.setTitle("Old Grade");
        existingGrade.setDescription("Old Desc");

        SemesterEntity semester1 = new SemesterEntity();
        semester1.setSemesterId("SEM001");
        semester1.setTitle("Old Semester");
        semester1.setDescription("Old Desc");
        semester1.setGradeEntity(existingGrade);
        existingGrade.setSemesterEntities(new ArrayList<>(List.of(semester1)));

        GradeEntity newGrade = new GradeEntity();
        newGrade.setGradeId("GRADE001");
        newGrade.setTitle("New Grade");
        newGrade.setDescription("New Desc");

        SemesterEntity newSemester1 = new SemesterEntity();
        newSemester1.setSemesterId("SEM001");
        newSemester1.setTitle("Updated Semester");
        newSemester1.setDescription("Updated Desc");

        SemesterEntity newSemester2 = new SemesterEntity();
        newSemester2.setSemesterId("SEM002");
        newSemester2.setTitle("New Semester");
        newSemester2.setDescription("New Desc");

        newGrade.setSemesterEntities(new ArrayList<>(Arrays.asList(newSemester1, newSemester2)));

        // 실행
        GradeEntity updatedGrade = curriculumUpdateMergeHelper.mergeSemester(existingGrade, newGrade);

        // 검증
        assertEquals("GRADE001", updatedGrade.getGradeId());
        assertEquals("Old Grade", updatedGrade.getTitle()); // mergeSemester는 title 업데이트 안 함
        assertEquals(2, updatedGrade.getSemesterEntities().size());

        SemesterEntity updatedSemester1 = updatedGrade.getSemesterEntities().stream()
                .filter(s -> s.getSemesterId().equals("SEM001"))
                .findFirst()
                .orElseThrow();
        assertEquals("Updated Semester", updatedSemester1.getTitle());
        assertEquals("Updated Desc", updatedSemester1.getDescription());
        assertEquals(existingGrade, updatedSemester1.getGradeEntity());


        assertEquals("New Semester", newSemester2.getTitle());
        assertEquals("New Desc", newSemester2.getDescription());
        assertEquals(existingGrade, newSemester2.getGradeEntity());
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