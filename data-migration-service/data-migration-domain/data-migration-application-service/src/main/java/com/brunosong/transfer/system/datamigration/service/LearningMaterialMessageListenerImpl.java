package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationStatusOutboxMessage;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.LearningMaterialMessageListener;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class LearningMaterialMessageListenerImpl implements LearningMaterialMessageListener {

    private final LearningMaterialDataMigrationHandler dataMigrationHandler;
    private final DataMigrationResponsePublisher dataMigrationResponsePublisher;

    public LearningMaterialMessageListenerImpl(LearningMaterialDataMigrationHandler dataMigrationHandler,
                                               DataMigrationResponsePublisher dataMigrationResponsePublisher) {
        this.dataMigrationHandler = dataMigrationHandler;
        this.dataMigrationResponsePublisher = dataMigrationResponsePublisher;
    }

    @Override
    public void migration(String dataMigrationInfoId, LearningMaterial learningMaterial) {
        dataMigrationHandler.convert();
        dataMigrationHandler.save();

        DataMigrationStatusOutboxMessage outboxMessage = DataMigrationStatusOutboxMessage.builder()
                .transferId(new TransferId(UUID.randomUUID()))
                .transferStatus(TransferStatus.SUCCESS)
                .build();

        dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);

    }

}
