package com.brunosong.transfer.system.ai.service.ports.output.repository;

import com.brunosong.transfer.system.ai.service.domain.entity.Course;

import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(Long courseSeq);
    Course save(Course course);
}
