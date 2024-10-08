package com.brunosong.transfer.system.transfer.dataaccess.chapter.adapter;

import com.brunosong.transfer.system.transfer.dataaccess.DataAccessTestConfiguration;
import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.dataaccess.chapter.mapper.ChapterDataAccessMapper;
import com.brunosong.transfer.system.transfer.dataaccess.chapter.repository.ChapterJpaRepository;
import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import com.brunosong.transfer.system.transfer.dataaccess.course.repository.CourseJpaRepository;
import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManager;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {DataAccessTestConfiguration.class,
        ChapterRepositoryImpl.class,
        ChapterDataAccessMapper.class})
@DataJpaTest
class ChapterRepositoryImplTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    CourseJpaRepository courseJpaRepository;

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
    void 정상적으로_CHATER_LIST를_가져온다() {

        List<Chapter> chapterList = chapterRepository.findByCourseSeq(courseSeq);
        Assertions.assertThat(chapterList).hasSize(2);

        // @Order Check
        Assertions.assertThat(chapterList.get(0).getChapOrder()).isEqualTo(1);
        Assertions.assertThat(chapterList.get(1).getChapOrder()).isEqualTo(2);
        Assertions.assertThat(chapterList.get(0).getChapTitle()).isEqualTo("테스트 코스 챕터 1");
        Assertions.assertThat(chapterList.get(1).getChapTitle()).isEqualTo("테스트 코스 챕터 2");

        Assertions.assertThat(chapterList.get(0).getChapSeq()).isNotNull();
        Assertions.assertThat(chapterList.get(1).getChapSeq()).isNotNull();

    }
}