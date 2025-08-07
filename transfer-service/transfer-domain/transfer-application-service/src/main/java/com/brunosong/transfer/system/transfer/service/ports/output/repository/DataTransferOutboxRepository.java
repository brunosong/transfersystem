package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataTransferOutboxRepository {

    DataTransferOutboxMessage save(DataTransferOutboxMessage dataTransferOutboxMessage);

    Optional<List<DataTransferOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                     OutboxStatus outboxStatus,
                                                                                     SagaStatus... sagaStatus);

    Optional<DataTransferOutboxMessage> findByTypeSagaIdAndSagaStatus(String type, UUID sagaId,
                                                                            SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatus);
}
