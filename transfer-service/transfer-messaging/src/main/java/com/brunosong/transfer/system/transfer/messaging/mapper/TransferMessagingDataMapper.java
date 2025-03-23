package com.brunosong.transfer.system.transfer.messaging.mapper;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialMetadataAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.SourceContentDataAvroModel;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.valueobject.LearningMaterialMetadata;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransferMessagingDataMapper {

    public DataMigrationRequestAvroModel toDataMigrationRequestAvroModel(Transfer transfer) {
        return DataMigrationRequestAvroModel.newBuilder()
                .setSagaId(UUID.randomUUID().toString())
                .setTransferId(transfer.getId().getValue().toString())
                .setSourceContentData(toSourceContentDataAvroModel(transfer.getSourceContentData()))
                .setDataMigrationId(transfer.getDataMigrationId().getValue())
                .build();
    }

    public SourceContentDataAvroModel toSourceContentDataAvroModel(SourceContentData sourceContentData) {
        return SourceContentDataAvroModel.newBuilder()
                .setSourceId(sourceContentData.getSourceId())
                .setJsonData(ByteBuffer.wrap(sourceContentData.getJsonData()))
                .build();
    }

    public DataMigrationResponse toDataMigrationResponse(DataMigrationResponseAvroModel dataMigrationResponseAvroModel) {
        return DataMigrationResponse.builder()
                .transferStatus(TransferStatus.valueOf(dataMigrationResponseAvroModel.getTransferStatus().name()))
                .transferId(UUID.fromString(dataMigrationResponseAvroModel.getTransferId()))
                .build();
    }

}
