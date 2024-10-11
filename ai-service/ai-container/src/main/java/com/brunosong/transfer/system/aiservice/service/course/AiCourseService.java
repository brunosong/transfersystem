package com.brunosong.transfer.system.aiservice.service.course;

import com.brunosong.transfer.system.aiservice.domain.AiCourse;
import com.brunosong.transfer.system.aiservice.service.course.port.AiCourseRepository;
import com.brunosong.transfer.system.aiservice.dto.course.AiCourseDto.AiCourseRespDto;
import com.brunosong.transfer.system.aiservice.dto.course.AiCourseDto.AiCourseSaveDto;
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
