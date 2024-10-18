package com.brunosong.transfer.system.transfer.service.dataaccess.material.mapper;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterialMetadata;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LearningMaterialDataAccessMapper {

    public LearningMaterial learningMaterialEntityToLearningMaterial(LearningMaterialViewEntity materialEntity) {
        return LearningMaterial.builder()
                .id(new LearningMaterialId(materialEntity.getId()))
                .title(materialEntity.getTitle())
                .metadataList(materialMetadataEntitiesToMaterialMetadataList(materialEntity.getMetadataList()))
                .build();
    }


    public List<LearningMaterialMetadata> materialMetadataEntitiesToMaterialMetadataList(
                                            List<Map<String,Object>> materialMetadataEntities) {

        return materialMetadataEntities.stream().map(map -> LearningMaterialMetadata.builder()
                                                                .materialId((String)map.get("materialId"))
                                                                .attributeName((String)map.get("attributeName"))
                                                                .attributeValue((String)map.get("attributeValue"))
                                                                .build())
                                                .collect(Collectors.toList());
    }


}
