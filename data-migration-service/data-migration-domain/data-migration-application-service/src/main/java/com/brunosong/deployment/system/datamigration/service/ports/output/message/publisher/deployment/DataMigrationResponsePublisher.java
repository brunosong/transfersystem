package com.brunosong.deployment.system.datamigration.service.ports.output.message.publisher.deployment;

import com.brunosong.deployment.system.datamigration.service.dto.message.ApplyResultMessage;

public interface DataMigrationResponsePublisher {

    void dataMigrationStatusPublish(ApplyResultMessage resultMessage);
}
