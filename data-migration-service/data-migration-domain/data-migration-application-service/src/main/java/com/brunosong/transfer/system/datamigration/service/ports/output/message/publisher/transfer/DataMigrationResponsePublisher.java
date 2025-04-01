package com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer;

import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationResponseMessage;

public interface DataMigrationResponsePublisher {
    void dataMigrationStatusPublish(DataMigrationResponseMessage responseMessage);
}
