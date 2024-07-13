package com.brunosong.transfersystem.aiservice.infrastructure.course;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("jpa-test")
class AiCourseJpaRepositoryTest {

    @Autowired
    AiCourseJpaRepository aiCourseJpaRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void AiCourseEntity_정상적으로_저장된다(){

        Long findSeq = 1L;

        AiCourseEntity entity = AiCourseEntity.builder()
                .aiCourseSeq(findSeq)
                .aiCourseName("테스트 코스")
                .build();

        aiCourseJpaRepository.save(entity);
        entityManager.flush();
        entityManager.clear();

        AiCourseEntity findEntity = aiCourseJpaRepository.findById(findSeq).get();

        Assertions.assertThat(findSeq).isEqualTo(findEntity.getAiCourseSeq());

    }

}