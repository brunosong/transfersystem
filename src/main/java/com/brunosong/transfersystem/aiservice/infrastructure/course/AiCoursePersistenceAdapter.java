package com.brunosong.transfersystem.aiservice.infrastructure.course;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import com.brunosong.transfersystem.aiservice.service.course.port.AiCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiCoursePersistenceAdapter implements AiCourseRepository {

    private final AiCourseJpaRepository aiCourseJpaRepository;

    @Override
    public Optional<AiCourse> findById(Long aiCourseSeq) {
        return aiCourseJpaRepository.findById(aiCourseSeq).map(AiCourseEntity::toModel);
    }

    @Override
    public Optional<AiCourse> save(AiCourse aiCourse) {
        return Optional.of(aiCourseJpaRepository.save(AiCourseEntity.fromModel(aiCourse)).toModel());
    }

}
