package com.brunosong.transfer.system.ai.service.dataaccess.course.adapter;

import com.brunosong.transfer.system.ai.service.dataaccess.course.entity.CourseEntity;
import com.brunosong.transfer.system.ai.service.dataaccess.course.mapper.CourseDataAccessMapper;
import com.brunosong.transfer.system.ai.service.dataaccess.course.repository.CourseJpaRepository;
import com.brunosong.transfer.system.ai.service.domain.entity.Course;
import com.brunosong.transfer.system.ai.service.ports.output.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

    private final CourseJpaRepository courseJpaRepository;
    private final CourseDataAccessMapper courseDataAccessMapper;

    @Override
    public Optional<Course> findById(Long courseSeq) {
        Optional<CourseEntity> courseEntity = courseJpaRepository.findById(courseSeq);
        return Optional.of(courseDataAccessMapper.courseEntityToCourse(courseEntity.get()));
    }

    @Override
    public Course save(Course course) {
        CourseEntity resultEntity = courseJpaRepository.save(courseDataAccessMapper.courseToCourseEntity(course));
        return courseDataAccessMapper.courseEntityToCourse(resultEntity);
    }
}
