package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.helper.MessageResponseHelper;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.datamigration.service.ports.output.cache.CurriculumCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferSendMessageListenerImpl implements DataMigrationMessageListener {

    private final DataMigrationInfoHandler dataMigrationInfoHandler;
    private final DataPersistHelper dataPersistHelper;
    private final MessageResponseHelper messageResponseHelper;


    @Override
    @Transactional
    public void migration(DataMigrationRequest dataMigrationRequest) {
        
        // 아직 구현 안됨
        DataMigration dataMigrationInfo = dataMigrationInfoHandler.findDataMigrationInfo(dataMigrationRequest.getDataMigrationId());

        try {
            dataPersistHelper.persist(dataMigrationRequest.getSourceContentData());
            messageResponseHelper.successResponseMessage(dataMigrationRequest);
        } catch (Exception e) {
            e.printStackTrace();
            messageResponseHelper.failsResponseMessage(dataMigrationRequest, e);
        }
    }

}
