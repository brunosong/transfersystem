package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.exception.CourseNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SaveTargetRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = TransferTestConfiguration.class)
class TransferSendRepositoryHelperTest {

    @Autowired
    TransferSendServiceHelper transferSendRepositoryHelper;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    SaveTargetRepository saveTargetRepository;

    private ExcutionTransferCommand excutionTransferCommand;
    private Course course;

    @BeforeAll
    public void init() {
        excutionTransferCommand = new ExcutionTransferCommand();
        Chapter chap1 = Chapter.builder()
                .courseSeq(1L)
                .chapTitle("테스트 챕터1")
                .build();

        List<Chapter> chapterList = new ArrayList<>();
        chapterList.add(chap1);

        course = new Course(1L, "테스트 코스1", chapterList);
        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
    }

    @Test
    void COURSE_정상적으로_로직이_흘러갔다() {
        transferSendRepositoryHelper.transferLearningLevelOne(excutionTransferCommand);
        verify(courseRepository,times(1)).findById(any());
        verify(saveTargetRepository,times(1)).saveMaterialLevelOne(course);
    }

    @Test
    void COURSELIST_값이없을때_EXCEPTION을_반환한다() {
        when(courseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> {
            transferSendRepositoryHelper.transferLearningLevelOne(excutionTransferCommand);
        });
    }


}