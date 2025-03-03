package com.brunosong.transfer.system.transfer.service.ports.input.message.listener;

import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;

public interface DataMigrationMessageListener {
    void transferStatusUpdate(DataMigrationResponse dataMigrationResponse);
}
