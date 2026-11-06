package com.brunosong.deployment.system.deployment.service.dataaccess.source.adapter;

import com.brunosong.deployment.system.dataaccess.materials.repository.LearningMaterialMongoRepository;
import com.brunosong.deployment.system.deployment.service.dataaccess.config.DynamicDataSourceConnector;
import com.brunosong.deployment.system.deployment.service.dataaccess.source.mapper.SourceDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class SourceRepositoryImpl implements SourceRepository {

    private final LearningMaterialMongoRepository learningMaterialMongoRepository;
    private final DynamicDataSourceConnector dynamicConnector;
    private final SourceDataAccessMapper sourceDataAccessMapper;

    public SourceRepositoryImpl(LearningMaterialMongoRepository learningMaterialMongoRepository, DynamicDataSourceConnector dynamicConnector, SourceDataAccessMapper sourceDataAccessMapper) {
        this.learningMaterialMongoRepository = learningMaterialMongoRepository;
        this.dynamicConnector = dynamicConnector;
        this.sourceDataAccessMapper = sourceDataAccessMapper;
    }

    @Override
    public Optional<SourceContentData> findData(String sourceId, SourceConfig sourceConfig) {
        if (sourceId == null) {
            throw new IllegalArgumentException("sourceId cannot be null");
        }

        return dynamicConnector.query(sourceConfig, sourceId)
                .map(r -> sourceDataAccessMapper.mapToSourceContentData(r, sourceId, sourceConfig));
    }
}
