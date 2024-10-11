package com.brunosong.transfer.system.aiservice.service.course.port;

import com.brunosong.transfer.system.aiservice.domain.AiCourse;

import java.util.List;
import java.util.Optional;

public interface AiCourseRepository {

    List<AiCourse> findAll();

    Optional<AiCourse> findById(Long aiCourseSeq);

    Optional<AiCourse> save(AiCourse aiCourse);

}
