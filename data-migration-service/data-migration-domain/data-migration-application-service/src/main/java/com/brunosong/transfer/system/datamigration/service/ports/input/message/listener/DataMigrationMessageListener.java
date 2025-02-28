package com.brunosong.transfer.system.datamigration.service.ports.input.message.listener;


import com.brunosong.transfer.system.datamigration.service.domain.entity.LearningMaterial;

public interface DataMigrationMessageListener {
    void migration(String dataMigrationInfoId, LearningMaterial learningMaterial);
}
