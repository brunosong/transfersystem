package com.brunosong.deployment.system.deployment.service.dataaccess.source.mapper;

import com.brunosong.deployment.system.deployment.service.dataaccess.source.entity.SourceConfigEntity;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocument;
import org.springframework.stereotype.Component;

@Component
public class SourceDataAccessMapper {

    public SourceDocument toSourceDocument(byte[] json, String sourceId, SourceConfig sourceConfig) {
        return new SourceDocument(sourceId, sourceConfig.getSourceType(), json);
    }

    public SourceConfig entityToSourceConfig(SourceConfigEntity entity) {
        return SourceConfig.builder()
                .sourceType(entity.getSourceType())
                .database(entity.getDatabase())
                .dbType(entity.getDbType())
                .host(entity.getHost())
                .password(entity.getPassword())
                .port(entity.getPort())
                .tableOrCollection(entity.getTableOrCollection())
                .username(entity.getUsername())
                .build();
    }

}

