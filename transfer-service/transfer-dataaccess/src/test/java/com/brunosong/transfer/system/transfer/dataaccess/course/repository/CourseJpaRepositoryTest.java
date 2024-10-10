package com.brunosong.transfer.system.transfer.dataaccess.course.repository;

import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.dataaccess.DataAccessTestConfiguration;
import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = DataAccessTestConfiguration.class)
@DataJpaTest
class CourseJpaRepositoryTest {

    @Autowired
    CourseJpaRepository courseJpaRepository;

    private CourseEntity course;

    @BeforeAll
    public void setup() {

        course = CourseEntity.builder()
                .courseName("테스트 코스")
                .build();

        ChapterEntity chapter1 = ChapterEntity.builder()
                .course(course)
                .chapTitle("테스트 코스 챕터 1")
                .build();

        course.addChapter(chapter1);
        courseJpaRepository.save(course);
    }

    @Test
    void 아이디로_검색하기() {
        Long findId = 1L;
        CourseEntity resultCourse = courseJpaRepository.findById(findId).get();
        Assertions.assertThat(resultCourse.getCourseSeq()).isEqualTo(findId);
    }
}