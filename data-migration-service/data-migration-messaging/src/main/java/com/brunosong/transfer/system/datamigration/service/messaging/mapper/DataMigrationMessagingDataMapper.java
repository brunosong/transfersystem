package com.brunosong.transfer.system.datamigration.service.messaging.mapper;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationResponseMessage;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.TransferStatus;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.SourceContentDataAvroModel;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationMessagingDataMapper {

    public DataMigrationRequest avroModelToDataMigrationRequest(DataMigrationRequestAvroModel dataMigrationRequestAvroModel) {
        return DataMigrationRequest.builder()
                .transferId(dataMigrationRequestAvroModel.getTransferId())
                .dataMigrationId(dataMigrationRequestAvroModel.getDataMigrationId())
                .sourceContentData(avroModelToSourceContentData(dataMigrationRequestAvroModel.getSourceContentData()))
                .build();
    }

    public SourceContentData avroModelToSourceContentData(SourceContentDataAvroModel sourceContentDataAvroModel) {
        return SourceContentData.builder()
                .sourceId(sourceContentDataAvroModel.getSourceId())
                .jsonData(sourceContentDataAvroModel.getJsonData())
                .build();
    }

    public DataMigrationResponseAvroModel toDataMigrationResponseAvroModel(DataMigrationResponseMessage responseMessage) {
        return DataMigrationResponseAvroModel.newBuilder()
                .setTransferId(responseMessage.getTransferId())
                .setMessage(responseMessage.getMessage())
                .setTransferStatus(TransferStatus.valueOf(responseMessage.getTransferStatus().name()))
                .build();
    }
}
