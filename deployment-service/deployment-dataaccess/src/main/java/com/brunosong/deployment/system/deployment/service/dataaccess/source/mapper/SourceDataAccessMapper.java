package com.brunosong.deployment.system.deployment.service.dataaccess.source.mapper;

import com.brunosong.deployment.system.deployment.service.dataaccess.source.entity.SourceConfigEntity;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceContentData;
import org.springframework.stereotype.Component;

@Component
public class SourceDataAccessMapper {

    public SourceContentData mapToSourceContentData(byte[] jsonData, String id, SourceConfig sourceConfig) {
        return SourceContentData.builder()
                            .sourceId(id)
                            .sourceType(sourceConfig.getSourceType())
                            .dbType(sourceConfig.getDbType())
                            .jsonData(jsonData)
                            .build();
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

