package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Course;

import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(Long courseSeq);
}
