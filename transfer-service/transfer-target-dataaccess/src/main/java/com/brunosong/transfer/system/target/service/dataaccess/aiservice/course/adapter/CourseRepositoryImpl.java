package com.brunosong.transfer.system.target.service.dataaccess.aiservice.course.adapter;

import com.brunosong.transfer.system.target.service.dataaccess.aiservice.course.repository.CourseJpaRepository;
import com.brunosong.transfer.system.target.service.dataaccess.aiservice.course.entity.CourseEntity;
import com.brunosong.transfer.system.target.service.dataaccess.aiservice.course.mapper.CourseDataAccessMapper;
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
    public Optional<Course> findById(Long courseSeq) {
        Optional<CourseEntity> courseEntity = courseJpaRepository.findById(courseSeq);
        return Optional.of(courseDataAccessMapper.courseEntityToCourse(courseEntity.get()));
    }
}
