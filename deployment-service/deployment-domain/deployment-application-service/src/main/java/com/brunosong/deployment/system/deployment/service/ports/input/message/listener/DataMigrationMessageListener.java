package com.brunosong.deployment.system.deployment.service.ports.input.message.listener;

import com.brunosong.deployment.system.deployment.service.dto.create.DataMigrationResponse;

public interface DataMigrationMessageListener {
    void deploymentStatusUpdate(DataMigrationResponse dataMigrationResponse);
}
