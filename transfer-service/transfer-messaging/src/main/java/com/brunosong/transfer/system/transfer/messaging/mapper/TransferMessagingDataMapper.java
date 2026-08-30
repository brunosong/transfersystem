package com.brunosong.transfer.system.transfer.messaging.mapper;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.SourceContentDataAvroModel;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

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

    /**
     * 아웃박스에 적혀 있던 페이로드를 그대로 발행 모델로 편다.
     * sagaId 는 페이로드가 아니라 아웃박스 행이 들고 있으므로 따로 받는다.
     */
    public DataMigrationRequestAvroModel toDataMigrationRequestAvroModel(DataTransferEventPayload payload,
                                                                        UUID sagaId) {
        return DataMigrationRequestAvroModel.newBuilder()
                .setSagaId(sagaId.toString())
                .setTransferId(payload.getTransferId().toString())
                .setSourceContentData(toSourceContentDataAvroModel(payload))
                .setDataMigrationId(payload.getDataMigrationId())
                .build();
    }

    public SourceContentDataAvroModel toSourceContentDataAvroModel(SourceContentData sourceContentData) {
        return SourceContentDataAvroModel.newBuilder()
                .setChunkOffset(sourceContentData.getChunkOffset())
                .setSourceId(sourceContentData.getSourceId())
                .setJsonData(ByteBuffer.wrap(sourceContentData.getJsonData()))
                .build();
    }

    public SourceContentDataAvroModel toSourceContentDataAvroModel(DataTransferEventPayload payload) {
        return SourceContentDataAvroModel.newBuilder()
                .setChunkOffset(payload.getChunkOffset())
                .setSourceId(payload.getSourceId())
                .setJsonData(ByteBuffer.wrap(payload.getChunkData()))
                .build();
    }

    public DataMigrationResponse toDataMigrationResponse(DataMigrationResponseAvroModel dataMigrationResponseAvroModel) {
        return DataMigrationResponse.builder()
                .chunkOffset(dataMigrationResponseAvroModel.getChunkOffset())
                .transferStatus(TransferStatus.valueOf(dataMigrationResponseAvroModel.getTransferStatus().name()))
                .transferId(new TransferId(UUID.fromString(dataMigrationResponseAvroModel.getTransferId())))
                .message(dataMigrationResponseAvroModel.getMessage())
                .build();
    }

}
