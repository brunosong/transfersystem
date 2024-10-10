package com.brunosong.transfer.system.transfer.dataaccess.course.adapter;

import com.brunosong.transfer.system.transfer.dataaccess.DataAccessTestConfiguration;
import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.dataaccess.chapter.mapper.ChapterDataAccessMapper;
import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import com.brunosong.transfer.system.transfer.dataaccess.course.mapper.CourseDataAccessMapper;
import com.brunosong.transfer.system.transfer.dataaccess.course.repository.CourseJpaRepository;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {DataAccessTestConfiguration.class, CourseDataAccessMapper.class,
                                 CourseRepositoryImpl.class,
                                 ChapterDataAccessMapper.class
})
@DataJpaTest
class CourseRepositoryImplTest {

    @Autowired
    private CourseRepositoryImpl courseRepository;

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    public Long courseSeq;

    @BeforeAll
    public void setup() {
        CourseEntity course = CourseEntity.builder()
                .courseName("테스트 코스")
                .build();

        ChapterEntity chapter1 = ChapterEntity.builder()
                .course(course)
                .chapTitle("테스트 코스 챕터 1")
                .build();
        ChapterEntity chapter2 = ChapterEntity.builder()
                .course(course)
                .chapTitle("테스트 코스 챕터 2")
                .build();
        course.addChapter(chapter1);
        course.addChapter(chapter2);

        CourseEntity save = courseJpaRepository.save(course);
        courseSeq = save.getCourseSeq();

    }

    @Test
    void COURSE_ENTITY_에서_COURSE_로_변경되어_가져온다() {
        Course course = courseRepository.findById(courseSeq);

        Assertions.assertThat(course.getChapterList()).hasSize(2);

        Assertions.assertThat(course.getCourseName()).isEqualTo("테스트 코스");
        Assertions.assertThat(course.getChapterList().get(0).getChapTitle()).isEqualTo("테스트 코스 챕터 1");
        Assertions.assertThat(course.getChapterList().get(1).getChapTitle()).isEqualTo("테스트 코스 챕터 2");
    }

}