package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Course;

public interface CourseRepository {
    Course findById(Long courseSeq);
}
