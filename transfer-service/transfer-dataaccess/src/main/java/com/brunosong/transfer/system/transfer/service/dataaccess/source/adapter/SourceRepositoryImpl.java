package com.brunosong.transfer.system.transfer.service.dataaccess.source.adapter;

import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialMongoRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper.SourceDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class SourceRepositoryImpl implements SourceRepository {

    private final LearningMaterialMongoRepository learningMaterialMongoRepository;
    private final SourceDataAccessMapper sourceDataAccessMapper;

    public SourceRepositoryImpl(LearningMaterialMongoRepository learningMaterialMongoRepository,
                                SourceDataAccessMapper sourceDataAccessMapper) {
        this.learningMaterialMongoRepository = learningMaterialMongoRepository;
        this.sourceDataAccessMapper = sourceDataAccessMapper;
    }

    @Override
    public Optional<SourceContentData> findData(String materialId) {
        return learningMaterialMongoRepository.findById(materialId)
                .map(sourceDataAccessMapper::entityViewToSourceContentData);
    }
}
