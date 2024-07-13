package com.brunosong.transfersystem.aiservice.service.course;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import com.brunosong.transfersystem.aiservice.dto.course.AiCourseDto.AiCourseSaveDto;
import com.brunosong.transfersystem.aiservice.service.course.port.AiCourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiCourseServiceTest {

    @Mock
    AiCourseRepository aiCourseRepository;

    @InjectMocks
    AiCourseService aiCourseService;


    @Test
    void findById로_값이_찾아져도_SAVE_메소드가_정상실행된다(){

        //given
        AiCourse aiCourse = AiCourse.builder().aiCourseSeq(1L).build();

        AiCourseSaveDto aiCourseSaveDto = new AiCourseSaveDto();
        aiCourseSaveDto.setAiCourseSeq(1L);
        aiCourseSaveDto.setAiCourseName("COURSE 1");

        //stub
        when(aiCourseRepository.findById(1L)).thenReturn(Optional.of(aiCourse));
        when(aiCourseRepository.save(any(AiCourse.class))).thenReturn(Optional.empty());

        //when
        aiCourseService.save(aiCourseSaveDto);

        //then
        verify(aiCourseRepository, times(1) ).findById(1L);
        verify(aiCourseRepository, times(1) ).save(aiCourse);

    }



    @Test
    void findById가_EMPTY_여도_SAVE_메소드가_정상실행된다(){

        //given
        AiCourseSaveDto aiCourseSaveDto = new AiCourseSaveDto();
        aiCourseSaveDto.setAiCourseSeq(1L);
        aiCourseSaveDto.setAiCourseName("COURSE 1");

        //stub
        when(aiCourseRepository.findById(1L)).thenReturn(Optional.empty());
        when(aiCourseRepository.save(any(AiCourse.class))).thenReturn(Optional.empty());

        //when
        aiCourseService.save(aiCourseSaveDto);

        //then
        verify(aiCourseRepository, times(1) ).findById(1L);
        verify(aiCourseRepository, times(1) ).save(any(AiCourse.class));

    }

}