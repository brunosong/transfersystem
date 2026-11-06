package com.brunosong.deployment.system.datamigration.service.ports.input.message.listener;

import com.brunosong.deployment.system.datamigration.service.dto.message.ApplyChunkRequest;

public interface DataMigrationMessageListener {

    void migration(ApplyChunkRequest request);
}
