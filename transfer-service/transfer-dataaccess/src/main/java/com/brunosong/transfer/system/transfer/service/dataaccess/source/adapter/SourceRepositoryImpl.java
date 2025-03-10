package com.brunosong.transfer.system.transfer.service.dataaccess.source.adapter;

import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialMongoRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.config.DynamicDataSourceConnector;
import com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper.SourceDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
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
    public Optional<SourceContentData> findData(String sourceId, SourceType sourceType) {
        if (sourceId == null) {
            throw new IllegalArgumentException("materialId cannot be null");
        }

        return dynamicConnector.query(sourceType, sourceId).map(r -> sourceDataAccessMapper.mapToSourceContentData(r, sourceId));
    }
}
