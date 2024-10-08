package com.brunosong.transfer.system.transfer.dataaccess.course.repository;

import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.dataaccess.DataAccessTestConfiguration;
import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = DataAccessTestConfiguration.class)
@DataJpaTest
class CourseJpaRepositoryTest {

    @Autowired
    CourseJpaRepository courseJpaRepository;

    @Test
    void SAVE_테스트() {
        CourseEntity course = CourseEntity.builder()
                .courseName("테스트 코스")
                .build();

        ChapterEntity chapter1 = ChapterEntity.builder()
                .course(course)
                .chapTitle("테스트 코스 챕터 1")
                .build();

        course.addChapter(chapter1);

        CourseEntity save = courseJpaRepository.save(course);

        Assertions.assertThat(save.getCourseSeq()).isNotNull();
        Assertions.assertThat(save.getChapterList().get(0).getChapSeq()).isNotNull();
    }
}