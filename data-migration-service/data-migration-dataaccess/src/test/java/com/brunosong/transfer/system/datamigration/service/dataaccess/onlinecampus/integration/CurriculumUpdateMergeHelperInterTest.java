package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.integration;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter.CurriculumUpdateMergeHelper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.CurriculumEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.GradeEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.SemesterEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@EntityScan(basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity")
@EnableJpaRepositories(basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository")
@Import(CurriculumUpdateMergeHelper.class)
public class CurriculumUpdateMergeHelperInterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    CurriculumUpdateMergeHelper curriculumUpdateMergeHelper;

    @Autowired
    private CurriculumJpaRepository curriculumJpaRepository;

    private CurriculumEntity existingCurriculum;
    private CurriculumEntity newCurriculum;
    private String curriculumId;

    @BeforeEach
    void setUp() {

        curriculumId = "CUR001";
        // 기존 CurriculumEntity 설정
        existingCurriculum = new CurriculumEntity();
        existingCurriculum.setCurriculumId(curriculumId);
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
        curriculumJpaRepository.saveAndFlush(existingCurriculum);
        entityManager.flush();
        entityManager.clear();


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
    void givenExistingCurriculum_whenMergeSemester_thenUpdateSuccess() {

        Optional<CurriculumEntity> byCurriculumId = curriculumJpaRepository.findByCurriculumId(curriculumId);
        CurriculumEntity curriculumEntity = byCurriculumId.get();

        // 실행
        CurriculumEntity updatedCurriculum = curriculumUpdateMergeHelper.curriculumUpdate(curriculumEntity, newCurriculum);

        curriculumJpaRepository.save(updatedCurriculum);
        entityManager.flush();
        entityManager.clear();

        CurriculumEntity updateResult = curriculumJpaRepository.findByCurriculumId(curriculumId).get();

        Assertions.assertThat(updateResult.getCurriculumId()).isEqualTo(curriculumId);
        Assertions.assertThat(updateResult.getTitle()).isEqualTo("New Title");
        Assertions.assertThat(updateResult.getGradeEntities().get(0).getTitle()).isEqualTo("Updated Grade 1");

    }

    @Test
    void givenExistingCurriculum_whenMergeCurriculum_thenUpdateSuccess() {

        Optional<CurriculumEntity> byCurriculumId = curriculumJpaRepository.findByCurriculumId(curriculumId);
        CurriculumEntity curriculumEntity = byCurriculumId.get();

        // 실행
        CurriculumEntity updatedCurriculum = curriculumUpdateMergeHelper.curriculumUpdate(curriculumEntity, newCurriculum);

        curriculumJpaRepository.save(updatedCurriculum);
        entityManager.flush();
        entityManager.clear();

        CurriculumEntity updateResult = curriculumJpaRepository.findByCurriculumId(curriculumId).get();

        Assertions.assertThat(updateResult.getCurriculumId()).isEqualTo(curriculumId);
        Assertions.assertThat(updateResult.getTitle()).isEqualTo("New Title");
        Assertions.assertThat(updateResult.getGradeEntities().get(0).getTitle()).isEqualTo("Updated Grade 1");

    }
}
