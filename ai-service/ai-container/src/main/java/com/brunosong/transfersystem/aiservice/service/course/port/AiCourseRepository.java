package com.brunosong.transfersystem.aiservice.service.course.port;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;

import java.util.List;
import java.util.Optional;

public interface AiCourseRepository {

    List<AiCourse> findAll();

    Optional<AiCourse> findById(Long aiCourseSeq);

    Optional<AiCourse> save(AiCourse aiCourse);

}
