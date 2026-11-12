package com.brunosong.deployment.system.deployment.service.dataaccess.source.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.config.DynamicDataSourceConnector;
import com.brunosong.deployment.system.deployment.service.dataaccess.source.mapper.SourceDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocument;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceRepositoryImpl implements SourceRepository {

    private final DynamicDataSourceConnector dynamicConnector;
    private final SourceDataAccessMapper sourceDataAccessMapper;

    @Override
    public Optional<SourceDocument> findData(String sourceId, SourceConfig sourceConfig) {
        if (sourceId == null) {
            throw new IllegalArgumentException("sourceId cannot be null");
        }

        return dynamicConnector.query(sourceConfig, sourceId)
                .map(json -> sourceDataAccessMapper.toSourceDocument(json, sourceId, sourceConfig));
    }
}
