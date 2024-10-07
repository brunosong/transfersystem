package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Course;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll();
}
