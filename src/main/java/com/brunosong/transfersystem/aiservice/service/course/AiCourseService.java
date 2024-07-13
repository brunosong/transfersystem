package com.brunosong.transfersystem.aiservice.service.course;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import com.brunosong.transfersystem.aiservice.dto.course.AiCourseDto.AiCourseSaveDto;
import com.brunosong.transfersystem.aiservice.service.course.port.AiCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiCourseService {

    private final AiCourseRepository courseRepository;

    @Transactional("aiServiceJpaTransactionManager")
    public void save(AiCourseSaveDto saveDto) {

        Optional<AiCourse> findAiCourse = courseRepository.findById(saveDto.getAiCourseSeq());

        AiCourse aiCourse = findAiCourse.map(info -> {
            info.updateAiCourseName(saveDto.getAiCourseName());
            return info;
        }).orElseGet(() -> saveDto.toDomain());

        courseRepository.save(aiCourse);

    }


}
