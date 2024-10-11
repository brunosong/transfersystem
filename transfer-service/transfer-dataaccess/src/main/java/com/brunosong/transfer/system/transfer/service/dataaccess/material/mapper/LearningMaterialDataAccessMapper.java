package com.brunosong.transfer.system.transfer.service.dataaccess.material.mapper;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialEntity;
import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialMetadataEntity;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterialMetadata;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LearningMaterialDataAccessMapper {

    public LearningMaterial learningMaterialEntityToLearningMaterial(LearningMaterialEntity materialEntity) {
        return LearningMaterial.builder()
                .id(materialEntity.getId())
                .title(materialEntity.getTitle())
                .metadataList(materialMetadataEntitiesToMaterialMetadataList(materialEntity.getMetadataList()))
                .build();
    }


    public List<LearningMaterialMetadata> materialMetadataEntitiesToMaterialMetadataList(
                                            List<LearningMaterialMetadataEntity> materialMetadataEntities) {

        return materialMetadataEntities.stream().map(entity -> LearningMaterialMetadata
                                                                .builder()
                                                                .materialId(entity.getLearningMaterial().getId())
                                                                .attributeName(entity.getAttributeName())
                                                                .attributeValue(entity.getAttributeValue())
                                                                .build())
                                                .collect(Collectors.toList());
    }


}
