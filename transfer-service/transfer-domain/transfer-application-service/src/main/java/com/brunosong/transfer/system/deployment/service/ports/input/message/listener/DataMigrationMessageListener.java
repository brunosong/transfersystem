package com.brunosong.transfer.system.deployment.service.ports.input.message.listener;

import com.brunosong.transfer.system.deployment.service.dto.create.DataMigrationResponse;

public interface DataMigrationMessageListener {
    void deploymentStatusUpdate(DataMigrationResponse dataMigrationResponse);
}
