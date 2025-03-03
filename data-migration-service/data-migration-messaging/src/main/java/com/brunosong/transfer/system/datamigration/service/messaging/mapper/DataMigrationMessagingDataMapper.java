package com.brunosong.transfer.system.datamigration.service.messaging.mapper;

import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationStatusOutboxMessage;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.TransferStatus;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationMessagingDataMapper {

    public LearningMaterial dataMigrationRequestAvroModelToLearningMaterial(DataMigrationRequestAvroModel dataMigrationRequestAvroModel) {
        return LearningMaterial.builder()
                .id(new LearningMaterialId(dataMigrationRequestAvroModel.getLearningMaterial().getId()))
                .build();
    }

    public DataMigrationRequest avroModelToDataMigrationRequest(DataMigrationRequestAvroModel dataMigrationRequestAvroModel) {
        return DataMigrationRequest.builder()
                .transferId(dataMigrationRequestAvroModel.getTransferId())
                .dataMigrationId(dataMigrationRequestAvroModel.getDataMigrationInfoId())
                .learningMaterial(learningMaterialAvroModelToLearningMaterial(dataMigrationRequestAvroModel.getLearningMaterial()))
                .build();
    }

    public LearningMaterial learningMaterialAvroModelToLearningMaterial(LearningMaterialAvroModel learningMaterialAvroModel) {
        return LearningMaterial.builder()
                .id(new LearningMaterialId(learningMaterialAvroModel.getId()))
                .description(learningMaterialAvroModel.getDescription())
                .build();
    }

    public DataMigrationResponseAvroModel toDataMigrationResponseAvroModel(DataMigrationStatusOutboxMessage outboxMessage) {
        return DataMigrationResponseAvroModel.newBuilder()
                .setTransferId(outboxMessage.getTransferId())
                .setTransferStatus(TransferStatus.valueOf(outboxMessage.getTransferStatus().name()))
                .build();
    }
}
