package com.brunosong.transfersystem.aiservice.service.course.port;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;

import java.util.Optional;

public interface AiCourseRepository {

    Optional<AiCourse> findById(Long aiCourseSeq);

    Optional<AiCourse> save(AiCourse aiCourse);

}
