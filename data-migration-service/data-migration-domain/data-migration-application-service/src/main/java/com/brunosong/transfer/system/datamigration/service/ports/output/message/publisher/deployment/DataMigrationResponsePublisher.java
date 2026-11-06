package com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.deployment;

import com.brunosong.transfer.system.datamigration.service.dto.message.ApplyResultMessage;

public interface DataMigrationResponsePublisher {

    void dataMigrationStatusPublish(ApplyResultMessage resultMessage);
}
