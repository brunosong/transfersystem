package com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper;

import com.brunosong.transfer.system.transfer.service.dataaccess.source.entity.SourceConfigEntity;
import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class SourceDataAccessMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SourceContentData mapToSourceContentData(Map<String, Object> data, String id, SourceConfig sourceConfig) {
        return SourceContentData.builder()
                            .sourceId(id)
                            .sourceType(sourceConfig.getSourceType())
                            .dbType(sourceConfig.getDbType())
                            .jsonData(convertData(data))
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

    public byte[] convertData(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        try {
            String jsonData = objectMapper.writeValueAsString(entity);
            return jsonData.getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert entity to JSON", e);
            throw new RuntimeException("Failed to convert entity to JSON", e);
        }
    }
}

