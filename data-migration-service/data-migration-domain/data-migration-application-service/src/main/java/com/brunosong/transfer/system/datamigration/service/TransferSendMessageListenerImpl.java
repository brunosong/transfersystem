package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.handler.DataMigrationInfoHandler;
import com.brunosong.transfer.system.datamigration.service.handler.DataPersistHandler;
import com.brunosong.transfer.system.datamigration.service.helper.MessageResponseHelper;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferSendMessageListenerImpl implements DataMigrationMessageListener {

    private final DataMigrationInfoHandler dataMigrationInfoHandler;
    private final DataPersistHandler dataPersistHandler;
    private final MessageResponseHelper messageResponseHelper;

    @Override
    public void migration(DataMigrationRequest dataMigrationRequest) {
        
        // DataSource 선택
        DataMigration dataMigrationInfo = dataMigrationInfoHandler.findDataMigrationInfo(dataMigrationRequest.getDataMigrationId());

        try {
            long result = dataPersistHandler.persist(dataMigrationInfo, dataMigrationRequest.getSourceContentData());
            messageResponseHelper.successResponseMessage(dataMigrationRequest);
        } catch (Exception e) {
            messageResponseHelper.failsResponseMessage(dataMigrationRequest, e);
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
