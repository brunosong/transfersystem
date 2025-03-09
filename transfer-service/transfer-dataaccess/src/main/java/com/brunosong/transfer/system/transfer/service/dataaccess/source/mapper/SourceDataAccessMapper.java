package com.brunosong.transfer.system.transfer.service.dataaccess.source.mapper;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.transfer.service.valueobject.LearningMaterialMetadata;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SourceDataAccessMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SourceContentData entityViewToSourceContentData(LearningMaterialViewEntity materialEntity) {
        return SourceContentData.builder()
                            .sourceId(materialEntity.getId())
                            .jsonData(convertData(materialEntity))
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

    public List<LearningMaterialMetadata> materialMetadataEntitiesToMaterialMetadataList(
                                            List<Map<String,Object>> materialMetadataEntities) {

        return materialMetadataEntities.stream().map(map -> LearningMaterialMetadata.builder()
                                                                .attributeName((String)map.get("attributeName"))
                                                                .attributeValue((String)map.get("attributeValue"))
                                                                .build())
                                                .collect(Collectors.toList());
    }


}
