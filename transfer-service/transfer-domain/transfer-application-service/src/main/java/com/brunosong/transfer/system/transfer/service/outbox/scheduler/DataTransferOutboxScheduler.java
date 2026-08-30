package com.brunosong.transfer.system.transfer.service.outbox.scheduler;

import com.brunosong.transfer.system.domain.DomainConstants;
import com.brunosong.transfer.system.outbox.OutboxScheduler;
import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.DataTransferRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferOutboxScheduler implements OutboxScheduler {

    private final DataTransferOutboxHelper dataTransferOutboxHelper;
    private final DataTransferOutboxPublisherHelper dataTransferOutboxPublisherHelper;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${transfer-service.outbox-scheduler.fixed-delay}",
            initialDelayString = "${transfer-service.outbox-scheduler.initial-delay}")
    public void processOutboxMessages() {
        Optional<List<DataTransferOutboxMessage>> outboxMessagesResponse = dataTransferOutboxHelper.getTransferOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED,
                        SagaStatus.STARTED, SagaStatus.COMPENSATED);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<DataTransferOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} DataTransferOutboxMessages with ids : {}, sending to message bus!", outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage ->
                            outboxMessage.getId().toString()).collect(Collectors.joining()));

            for (DataTransferOutboxMessage outboxMessage : outboxMessages) {
                dataTransferOutboxPublisherHelper.publish(outboxMessage, this::updateOutboxStatus);
            }
        }
    }

    private void updateOutboxStatus(DataTransferOutboxMessage dataTransferOutboxMessage, OutboxStatus outboxStatus) {
        dataTransferOutboxMessage.setOutboxStatus(outboxStatus);
        // 언제 나갔는지가 없으면 밀린 건과 방금 나간 건을 구별할 수 없다
        dataTransferOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
        dataTransferOutboxHelper.save(dataTransferOutboxMessage);
        log.info("Updated DataTransferOutboxMessage with ID: {} to status: {}", dataTransferOutboxMessage.getId(), outboxStatus);

    }
}
