package com.brunosong.transfer.system.datamigration.service.messaging.mapper;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import org.springframework.stereotype.Component;

@Component
public class LoadTargetMessagingDataMapper {

    public LearningMaterial dataMigrationRequestAvroModelToLearningMaterial(DataMigrationRequestAvroModel dataMigrationRequestAvroModel) {

        return LearningMaterial.builder()
                .id(new LearningMaterialId(dataMigrationRequestAvroModel.getLearningMaterial().getId()))
                .build();

    }
}
