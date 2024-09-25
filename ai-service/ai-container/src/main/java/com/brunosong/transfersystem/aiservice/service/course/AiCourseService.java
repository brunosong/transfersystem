package com.brunosong.transfersystem.aiservice.service.course;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import com.brunosong.transfersystem.aiservice.dto.course.AiCourseDto.AiCourseRespDto;
import com.brunosong.transfersystem.aiservice.dto.course.AiCourseDto.AiCourseSaveDto;
import com.brunosong.transfersystem.aiservice.service.course.port.AiCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiCourseService {

    private final AiCourseRepository courseRepository;

    public List<AiCourseRespDto> findAll() {
        List<AiCourse> aiCourseList = courseRepository.findAll();
        return aiCourseList.stream()
                            .map(AiCourseRespDto::fromDomain).collect(Collectors.toList());
    }

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
