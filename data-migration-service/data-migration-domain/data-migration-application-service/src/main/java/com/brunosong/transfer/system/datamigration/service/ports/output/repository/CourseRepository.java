package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.entity.Course;

import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(Long courseSeq);
    Course save(Course course);
}
