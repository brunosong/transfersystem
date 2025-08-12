package com.brunosong.transfer.system.transfer.service.outbox.scheduler;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferApiSendHelper;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.DataTransferRequestMessagePublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.DataTransferOutboxRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.brunosong.transfer.system.saga.transfer.SagaConstants.TRANSFER_SAGA_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferOutboxPublisherHelper {

    private final DataTransferRequestMessagePublisher dataTransferRequestMessagePublisher;
    private final TransferApiSendHelper transferApiSendHelper;

    public void publish(DataTransferOutboxMessage outboxMessage, BiConsumer<DataTransferOutboxMessage, OutboxStatus> outboxCallback) {

        // 1. 전송할 데이터 설정 정보 찾기
        if (outboxMessage.getTransType() == TransType.API) {
            transferApiSendHelper.transferAction(null);
            log.info("API transfer completed: {}", "");
        } else if (outboxMessage.getTransType() == TransType.MESSAGING) {
            dataTransferRequestMessagePublisher.publish(outboxMessage, outboxCallback);
            log.info("Messaging transfer completed: {}", "");
        }

    }

}
