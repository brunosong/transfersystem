package com.brunosong.transfer.system.transfer.messaging.mapper;

import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialMetadataAvroModel;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterialMetadata;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransferMessagingDataMapper {

    public LearningMaterialAvroModel learningMaterialToLearningMaterialAvroModel(LearningMaterial learningMaterial) {
        return LearningMaterialAvroModel.newBuilder()
                .setId(learningMaterial.getId().getValue())
                .setTitle(learningMaterial.getTitle())
                .setDescription(learningMaterial.getDescription())
                .setMetadataList(learningMaterialMetadataToLearningMaterialMetadataAvro(learningMaterial.getMetadataList()))
                .build();
    }

    public List<LearningMaterialMetadataAvroModel> learningMaterialMetadataToLearningMaterialMetadataAvro(List<LearningMaterialMetadata> metadata) {
        return metadata.stream().map( meta ->
                LearningMaterialMetadataAvroModel.newBuilder()
                    .setId(meta.getMaterialId())
                    .setMaterialId(meta.getMaterialId())
                    .setAttributeName(meta.getAttributeName())
                    .setAttributeValue(meta.getAttributeValue())
                    .build()
        ).collect(Collectors.toList());
    }

}
