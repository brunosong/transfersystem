package com.brunosong.transfer.system.datamigration.service.messaging.mapper;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import org.springframework.stereotype.Component;

@Component
public class LoadTargetMessagingDataMapper {

    public LearningMaterial learningMaterialAvroModelToLearningMaterial(LearningMaterialAvroModel learningMaterialAvroModel) {

        return LearningMaterial.builder()
                .id(new LearningMaterialId(learningMaterialAvroModel.getId()))
                .build();

    }
}
