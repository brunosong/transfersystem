package com.brunosong.transfer.system.transfer.service.outbox.scheduler;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.DataTransferOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brunosong.transfer.system.saga.transfer.SagaConstants.TRANSFER_SAGA_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferOutboxHelper {

    private final DataTransferOutboxRepository dataTransferOutboxRepository;

    @Transactional(readOnly = true)
    public Optional<List<DataTransferOutboxMessage>> getTransferOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                                         SagaStatus... sagaStatus) {
        return dataTransferOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(TRANSFER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<DataTransferOutboxMessage> getTransferOutboxMessageBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return dataTransferOutboxRepository.findByTypeSagaIdAndSagaStatus(TRANSFER_SAGA_NAME, sagaId, sagaStatus);
    }

    public void save(DataTransferOutboxMessage dataTransferOutboxMessage) {

        DataTransferOutboxMessage response = dataTransferOutboxRepository.save(dataTransferOutboxMessage);

        if (response == null) {
            log.error("Failed to save DataTransferOutboxMessage: {}", dataTransferOutboxMessage);
            throw new TransferDomainException("Failed to save DataTransferOutboxMessage: " + dataTransferOutboxMessage);
        } else {
            log.info("DataTransferOutboxMessage saved successfully: {}", response);
        }

    }

}
