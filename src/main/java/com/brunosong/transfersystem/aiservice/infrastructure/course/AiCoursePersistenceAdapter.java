package com.brunosong.transfersystem.aiservice.infrastructure.course;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import com.brunosong.transfersystem.aiservice.service.course.port.AiCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AiCoursePersistenceAdapter implements AiCourseRepository {

    private final AiCourseJpaRepository aiCourseJpaRepository;

    @Override
    public List<AiCourse> findAll() {
        return aiCourseJpaRepository.findAll().stream()
                .map(AiCourseEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<AiCourse> findById(Long aiCourseSeq) {
        return aiCourseJpaRepository.findById(aiCourseSeq).map(AiCourseEntity::toModel);
    }

    @Override
    public Optional<AiCourse> save(AiCourse aiCourse) {
        return Optional.of(aiCourseJpaRepository.save(AiCourseEntity.fromModel(aiCourse)).toModel());
    }

}
