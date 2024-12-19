package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.LearningMaterialMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LearningMaterialMessageListenerImpl implements LearningMaterialMessageListener {

    private final LearningMaterialDataMigrationHandler dataMigrationHandler;

    public LearningMaterialMessageListenerImpl(LearningMaterialDataMigrationHandler dataMigrationHandler) {
        this.dataMigrationHandler = dataMigrationHandler;
    }

    @Override
    public void migration(LearningMaterial learningMaterial) {
        dataMigrationHandler.convert();
        dataMigrationHandler.save();
    }

}
