package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.event.EmptyEvent;
import com.brunosong.transfer.system.saga.SagaStep;
import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DataMigrationSaga implements SagaStep<DataMigrationResponse, EmptyEvent , EmptyEvent> {

    @Override
    @Transactional
    public EmptyEvent process(DataMigrationResponse dataMigrationResponse) {
        log.info("Processing data migration with response: {}", dataMigrationResponse);
        // Here you would implement the logic to handle the data migration
        // For now, we just log the response and return an empty event
        return null;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(DataMigrationResponse dataMigrationResponse) {
        log.warn("Rolling back data migration for response: {}", dataMigrationResponse);
        // Implement rollback logic if necessary
        return null;
    }

}
