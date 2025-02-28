package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationStatusOutboxMessage;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class TransferSendMessageListenerImpl implements DataMigrationMessageListener {

    private final DataPersistHandler dataPersistHandler;
    private final DataMigrationResponsePublisher dataMigrationResponsePublisher;
    private final DataMigrationInfoCreateHandler dataMigrationInfoCreateHandler;

    public TransferSendMessageListenerImpl(DataPersistHandler dataPersistHandler,
                                           DataMigrationResponsePublisher dataMigrationResponsePublisher, DataMigrationInfoCreateHandler dataMigrationInfoCreateHandler) {
        this.dataPersistHandler = dataPersistHandler;
        this.dataMigrationResponsePublisher = dataMigrationResponsePublisher;
        this.dataMigrationInfoCreateHandler = dataMigrationInfoCreateHandler;
    }

    @Override
    public void migration(String dataMigrationInfoId, LearningMaterial learningMaterial) {

        dataPersistHandler.convert();
        dataPersistHandler.save();

        // 마이그레이션 정보 저장
        dataMigrationInfoCreateHandler.persistMigrationInfo(null);

        // 리스폰스 메시징
        DataMigrationStatusOutboxMessage outboxMessage = DataMigrationStatusOutboxMessage.builder()
                .transferId(new TransferId(UUID.randomUUID()))
                .transferStatus(TransferStatus.SUCCESS)
                .build();

        dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);

    }

}
