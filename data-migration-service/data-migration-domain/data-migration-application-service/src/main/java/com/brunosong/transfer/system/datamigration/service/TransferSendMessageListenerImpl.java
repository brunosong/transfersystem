package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationStatusOutboxMessage;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TransferSendMessageListenerImpl implements DataMigrationMessageListener {

    private final DataPersistHelper dataPersistHelper;
    private final DataMigrationResponsePublisher dataMigrationResponsePublisher;
    private final DataMigrationInfoCreateHandler dataMigrationInfoCreateHandler;
    private final DataMigrationInfoHandler dataMigrationInfoHandler;

    public TransferSendMessageListenerImpl(DataPersistHelper dataPersistHelper,
                                           DataMigrationResponsePublisher dataMigrationResponsePublisher, DataMigrationInfoCreateHandler dataMigrationInfoCreateHandler, DataMigrationInfoHandler dataMigrationInfoHandler) {
        this.dataPersistHelper = dataPersistHelper;
        this.dataMigrationResponsePublisher = dataMigrationResponsePublisher;
        this.dataMigrationInfoCreateHandler = dataMigrationInfoCreateHandler;
        this.dataMigrationInfoHandler = dataMigrationInfoHandler;
    }

    @Override
    @Transactional
    public void migration(DataMigrationRequest dataMigrationRequest) {

        DataMigration dataMigrationInfo = dataMigrationInfoHandler.findDataMigrationInfo(dataMigrationRequest.getDataMigrationId());

        dataPersistHelper.convert(dataMigrationInfo);

        try {
            Thread.sleep(10000);
            // dataPersistHelper.save(); // 예외 발생 시 롤백
            // dataMigrationInfoCreateHandler.persistMigrationInfo(null); // 마이그레이션 정보 저장

            // 성공 시 메시지
            DataMigrationStatusOutboxMessage outboxMessage = DataMigrationStatusOutboxMessage.builder()
                    .transferId(dataMigrationRequest.getTransferId())
                    .transferStatus(TransferStatus.SUCCESS)
                    .build();
            dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);
        } catch (Exception e) {
            // 실패 시 메시지
            DataMigrationStatusOutboxMessage outboxMessage = DataMigrationStatusOutboxMessage.builder()
                    .transferId(dataMigrationRequest.getTransferId())
                    .transferStatus(TransferStatus.FAILED)
                    .build();
            dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);
            throw new RuntimeException(); // 트랜잭션 롤백 유도
        }

    }

}
