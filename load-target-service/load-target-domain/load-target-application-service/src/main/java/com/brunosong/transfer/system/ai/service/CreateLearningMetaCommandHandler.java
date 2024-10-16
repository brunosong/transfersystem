package com.brunosong.transfer.system.ai.service;

import com.brunosong.transfer.system.ai.service.domain.entity.Course;
import com.brunosong.transfer.system.ai.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.ai.service.dto.create.CreateLearningMetaResponse;
import com.brunosong.transfer.system.ai.service.mapper.CreateLearningMetaDataMapper;
import com.brunosong.transfer.system.ai.service.ports.output.repository.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class CreateLearningMetaCommandHandler {
    private final CourseRepository courseRepository;
    private final CreateLearningMetaDataMapper aiCreateLearningMetaDataMapper;

    public CreateLearningMetaCommandHandler(CourseRepository courseRepository,
                                            CreateLearningMetaDataMapper aiCreateLearningMetaDataMapper) {
        this.courseRepository = courseRepository;
        this.aiCreateLearningMetaDataMapper = aiCreateLearningMetaDataMapper;
    }

    @Transactional
    public CreateLearningMetaResponse persistLearningMeta(CreateLearningMetaCommand createLearningMetaCommand) {
        Course course = aiCreateLearningMetaDataMapper.learningMaterialToCourse(
                createLearningMetaCommand.getLearningMaterial()
        );

        courseRepository.save(course);

        return CreateLearningMetaResponse.builder()
                .message("SUCCESS")
                .build();
    }
}
