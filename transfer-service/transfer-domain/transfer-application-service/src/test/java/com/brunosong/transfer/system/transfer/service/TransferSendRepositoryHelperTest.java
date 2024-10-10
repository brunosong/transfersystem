package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SaveTargetRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = TransferTestConfiguration.class)
class TransferSendRepositoryHelperTest {

    @Autowired
    TransferSendRepositoryHelper transferSendRepositoryHelper;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    SaveTargetRepository saveTargetRepository;

    private ExcutionTransferCommand excutionTransferCommand;
    private List<Course> courseList;

    @BeforeAll
    public void init() {
//        excutionTransferCommand = new ExcutionTransferCommand();
//        courseList = new ArrayList<>();
//        courseList.add(new Course(1L, "테스트 코스1"));
//
//        when(courseRepository.findByUpSeq(any())).thenReturn(courseList);
    }

    @Test
    void COURSE_정상적으로_로직이_흘러갔다() {
//        transferSendRepositoryHelper.courseTransferProcess(excutionTransferCommand);
//        verify(courseRepository,times(1)).findByUpSeq(any());
//        verify(saveTargetRepository,times(1)).saveCourses(courseList);
    }

    @Test
    void COURSELIST_값이없을때_EXCEIPTION을_반환한다() {
//        when(courseRepository.findByUpSeq(any())).thenReturn(Collections.emptyList());
//
//        assertThrows(CourseNotFoundException.class, () -> {
//            transferSendRepositoryHelper.courseTransferProcess(excutionTransferCommand);
//        });
//
//        verify(courseRepository,times(1)).findByUpSeq(any());
    }


}