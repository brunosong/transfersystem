package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.integration;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter.CurriculumDummy;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter.CurriculumPersistHandler;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter.CurriculumUpdateMergeHelper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.mapper.OnlineCampusDataAccessMapper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.SubjectJpaRepository;
import jakarta.persistence.EntityManager;
import org.bson.Document;
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
@EntityScan(basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity")
@EnableJpaRepositories(basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository")
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

        Document document = new Document();
        document.append("semesterId", "SEM001");
        Document subjectDocument = new Document();
        subjectDocument.append("id", "SUB001");
        document.append("subject", subjectDocument);

        // When
        curriculumPersistHandler.subjectToLesson(document);

        SubjectEntity result = subjectJpaRepository.findBySubjectId(subjectDocument.getString("id")).get();

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

        Document document = new Document();
        document.append("semesterId", "SEM001");
        Document subjectDocument = new Document();
        subjectDocument.append("id", "SUB001");
        document.append("subject", subjectDocument);

        // When
        curriculumPersistHandler.subjectToLesson(document);

        // Then
        SubjectEntity result = subjectJpaRepository.findBySubjectId(subjectDocument.getString("id")).get();

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