package com.brunosong.transfer.system.transfer.service.outbox.scheduler;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.DataTransferOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<List<DataTransferOutboxMessage>> getTransferOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                                         SagaStatus... sagaStatus) {
        return dataTransferOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(TRANSFER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<DataTransferOutboxMessage> getTransferOutboxMessageBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return dataTransferOutboxRepository.findByTypeSagaIdAndSagaStatus(TRANSFER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(DataTransferOutboxMessage dataTransferOutboxMessage) {

        DataTransferOutboxMessage response = dataTransferOutboxRepository.save(dataTransferOutboxMessage);

        if (response == null) {
            log.error("Failed to save DataTransferOutboxMessage: {}", dataTransferOutboxMessage);
            throw new TransferDomainException("Failed to save DataTransferOutboxMessage: " + dataTransferOutboxMessage);
        } else {
            log.info("DataTransferOutboxMessage saved successfully: {}", response);
        }

    }

    @Transactional
    public void saveDataTransferOutboxMessage(DataTransferEventPayload dataTransferEventPayload, UUID sagaId) {
        save(DataTransferOutboxMessage.builder()
                .id(UUID.randomUUID())
                .type(TRANSFER_SAGA_NAME)
                .transType(dataTransferEventPayload.getTransType())
                .sagaId(sagaId)
                .payload(createPayload(dataTransferEventPayload))
                .createdAt(dataTransferEventPayload.getCreatedAt())
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .build());
    }

    private String createPayload(DataTransferEventPayload dataTransferEventPayload) {
        try {
            return objectMapper.writeValueAsString(dataTransferEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create JSON payload for DataTransferEventPayload: {}", dataTransferEventPayload, e);
            throw new TransferDomainException("Could not create JSON payload for DataTransferEventPayload: " + dataTransferEventPayload, e);
        }
    }

}
