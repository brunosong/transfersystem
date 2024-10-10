package com.brunosong.transfer.system.transfer.dataaccess.course.adapter;

import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import com.brunosong.transfer.system.transfer.dataaccess.course.mapper.CourseDataAccessMapper;
import com.brunosong.transfer.system.transfer.dataaccess.course.repository.CourseJpaRepository;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

    private final CourseJpaRepository courseJpaRepository;
    private final CourseDataAccessMapper courseDataAccessMapper;

    @Override
    public Course findById(Long courseSeq) {
        Optional<CourseEntity> courseEntity = courseJpaRepository.findById(courseSeq);
        return courseDataAccessMapper.courseEntityToCourse(courseEntity.get());
    }
}
