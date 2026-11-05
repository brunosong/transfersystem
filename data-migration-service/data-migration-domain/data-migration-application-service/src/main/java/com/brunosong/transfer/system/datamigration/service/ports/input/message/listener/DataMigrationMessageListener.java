package com.brunosong.transfer.system.datamigration.service.ports.input.message.listener;

import com.brunosong.transfer.system.datamigration.service.dto.message.ApplyChunkRequest;

public interface DataMigrationMessageListener {

    void migration(ApplyChunkRequest request);
}
