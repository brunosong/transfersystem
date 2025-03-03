package com.brunosong.transfer.system.transfer.messaging.mapper;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialMetadataAvroModel;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterialMetadata;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransferMessagingDataMapper {

    public DataMigrationRequestAvroModel toDataMigrationRequestAvroModel(Transfer transfer, LearningMaterial learningMaterial) {
        return DataMigrationRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTransferId(transfer.getId().getValue().toString())
                .setLearningMaterial(learningMaterialToLearningMaterialAvroModel(learningMaterial))
                .setDataMigrationInfoId(transfer.getDataMigrationInfoId().getValue().toString())
                .build();
    }

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
                    .setAttributeName(meta.getAttributeName())
                    .setAttributeValue(meta.getAttributeValue())
                    .build()
        ).collect(Collectors.toList());
    }

    public DataMigrationResponse toDataMigrationResponse(DataMigrationResponseAvroModel dataMigrationResponseAvroModel) {
        return DataMigrationResponse.builder()
                .transferStatus(TransferStatus.valueOf(dataMigrationResponseAvroModel.getTransferStatus().name()))
                .transferId(UUID.fromString(dataMigrationResponseAvroModel.getTransferId()))
                .build();
    }

}
