package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLearningMetaResponse;
import com.brunosong.transfer.system.datamigration.service.mapper.CreateLearningMetaDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class CreateLearningMetaCommandHandler {
    private final CurriculumRepository curriculumRepository;
    private final CreateLearningMetaDataMapper aiCreateLearningMetaDataMapper;

    public CreateLearningMetaCommandHandler(CurriculumRepository curriculumRepository,
                                            CreateLearningMetaDataMapper aiCreateLearningMetaDataMapper) {
        this.curriculumRepository = curriculumRepository;
        this.aiCreateLearningMetaDataMapper = aiCreateLearningMetaDataMapper;
    }

    @Transactional
    public CreateLearningMetaResponse persistLearningMeta(CreateLearningMetaCommand createLearningMetaCommand) {
//        Course course = aiCreateLearningMetaDataMapper.learningMaterialToCourse(
//                createLearningMetaCommand.getLearningMaterial()
//        );
//
//        courseRepository.save(course);

        log.info("Transfer Success");

        return CreateLearningMetaResponse.builder()
                .message("SUCCESS")
                .build();
    }
}
