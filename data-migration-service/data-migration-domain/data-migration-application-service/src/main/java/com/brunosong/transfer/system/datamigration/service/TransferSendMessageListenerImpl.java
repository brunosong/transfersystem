package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationResponseMessage;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferSendMessageListenerImpl implements DataMigrationMessageListener {

    private final DataMigrationResponsePublisher dataMigrationResponsePublisher;
    private final DataMigrationInfoHandler dataMigrationInfoHandler;
    private final DataPersistHelper dataPersistHelper;

    @Override
    @Transactional
    public void migration(DataMigrationRequest dataMigrationRequest) {
        
        // 아직 구현 안됨
        DataMigration dataMigrationInfo = dataMigrationInfoHandler.findDataMigrationInfo(dataMigrationRequest.getDataMigrationId());

        try {

            dataPersistHelper.persist(dataMigrationRequest.getSourceContentData());

            // 성공 시 메시지
            DataMigrationResponseMessage outboxMessage = DataMigrationResponseMessage.builder()
                    .transferId(dataMigrationRequest.getTransferId())
                    .message("SUCCESS")
                    .transferStatus(TransferStatus.SUCCESS)
                    .build();

            dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);

            log.info("migration success !! transferId is {}, dataMigrationId : {}",
                    dataMigrationRequest.getTransferId(), dataMigrationRequest.getDataMigrationId());

        } catch (Exception e) {

            log.error("migration exception! transferId is {}, dataMigrationId : {}",
                    dataMigrationRequest.getTransferId(), dataMigrationRequest.getDataMigrationId());

            // DLT 대신 처리 실패 메시지 전송
            DataMigrationResponseMessage outboxMessage = DataMigrationResponseMessage.builder()
                    .transferId(dataMigrationRequest.getTransferId())
                    .message(e.getMessage())
                    .transferStatus(TransferStatus.FAILED)
                    .build();
            dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);
        }

    }

}
