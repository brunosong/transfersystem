package com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.integration;

import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.adapter.CurriculumDummy;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.adapter.CurriculumPersistHandler;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.adapter.CurriculumUpdateMergeHelper;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.mapper.OnlineCampusDataAccessMapper;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.repository.SubjectJpaRepository;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyOutcome;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@EntityScan(basePackages = "com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.entity")
@EnableJpaRepositories(basePackages = "com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.repository")
@Import({CurriculumPersistHandler.class, OnlineCampusDataAccessMapper.class, CurriculumUpdateMergeHelper.class})
class CurriculumPersistHandlerTest extends CurriculumDummy {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    CurriculumPersistHandler curriculumPersistHandler;

    @Autowired
    private CurriculumJpaRepository curriculumJpaRepository;

    @MockBean
    OnlineCampusDataAccessMapper dataAccessMapper;

    @SpyBean
    CurriculumUpdateMergeHelper curriculumUpdateMergeHelper;

    @Autowired
    SubjectJpaRepository subjectJpaRepository;


    @Test
    void test_save() {

        // Given
        CurriculumEntity curriculumEntity = createCurriculum();
        curriculumJpaRepository.save(curriculumEntity);
        entityManager.flush();
        entityManager.clear();

        SubjectEntity newSubjectEntity = createNewSemester().getSubjectEntities().get(0);
        Mockito.when(dataAccessMapper.convertToSubject(Mockito.any(),Mockito.any())).thenReturn(newSubjectEntity);

        ChunkContent.SubjectChunk chunk = new ChunkContent.SubjectChunk("GRD001", "SEM001",
                new ChunkContent.Subject("SUB001", null, null, List.of()));

        // When
        curriculumPersistHandler.subjectToLesson(chunk);

        SubjectEntity result = subjectJpaRepository.findBySubjectId("SUB001").get();

        assertEquals("New Description", result.getDescription());
        assertEquals("New Subject", result.getTitle());
        assertEquals(1, result.getUnitEntities().size());

        UnitEntity unitEntity = result.getUnitEntities().get(0);
        assertEquals(2, unitEntity.getLessonEntities().size());
        assertEquals("New Unit", unitEntity.getTitle());
        assertEquals("New Unit Desc", unitEntity.getDescription());
    }


    @Test
    void test_update() {

        // Given
        CurriculumEntity curriculumEntity = createCurriculum();
        SemesterEntity semester = createSemester();
        GradeEntity gradeEntity = curriculumEntity.getGradeEntities().get(0);
        gradeEntity.setSemesterEntities(new ArrayList<>(List.of(semester)));

        curriculumJpaRepository.save(curriculumEntity);
        entityManager.flush();
        entityManager.clear();

        SubjectEntity newSubjectEntity = createNewSemester().getSubjectEntities().get(0);
        Mockito.when(dataAccessMapper.convertToSubject(Mockito.any(),Mockito.any())).thenReturn(newSubjectEntity);

        ChunkContent.SubjectChunk chunk = new ChunkContent.SubjectChunk("GRD001", "SEM001",
                new ChunkContent.Subject("SUB001", null, null, List.of()));

        // When
        curriculumPersistHandler.subjectToLesson(chunk);

        // Then
        SubjectEntity result = subjectJpaRepository.findBySubjectId("SUB001").get();

        Mockito.verify(curriculumUpdateMergeHelper, Mockito.times(1)).mergeSubject(Mockito.any(SubjectEntity.class),
                                                                                                         Mockito.any(SubjectEntity.class));
        assertEquals("New Description", result.getDescription());
        assertEquals("New Subject", result.getTitle());

        UnitEntity unitEntity = result.getUnitEntities().get(0);
        assertEquals(2, unitEntity.getLessonEntities().size());
        assertEquals("New Unit", unitEntity.getTitle());
        assertEquals("New Unit Desc", unitEntity.getDescription());
    }



}