package com.brunosong.transfer.system.datamigration.service.ports.input.message.listener;


import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;

public interface DataMigrationMessageListener {
    void migration(DataMigrationRequest dataMigrationRequest);
}
