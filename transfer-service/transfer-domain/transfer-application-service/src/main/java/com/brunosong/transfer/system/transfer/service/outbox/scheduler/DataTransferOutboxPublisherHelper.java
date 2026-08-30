package com.brunosong.transfer.system.transfer.service.outbox.scheduler;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
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

    public void publish(DataTransferOutboxMessage outboxMessage, BiConsumer<DataTransferOutboxMessage, OutboxStatus> outboxCallback) {

        TransType transType = outboxMessage.getTransType();

        if (transType == TransType.MESSAGING) {
            // 상태를 닫는 일은 어댑터가 브로커 응답을 받은 뒤에 한다
            dataTransferRequestMessagePublisher.publish(outboxMessage, outboxCallback);
        } else {
            // API 경로는 아직 아웃박스에서 보낼 수 없다. 아웃박스 행에는 Transfer 가 없고
            // TransferApiSendHelper 는 Transfer 를 받는다. 여기서 상태를 닫지 않으면
            // 다음 주기가 같은 행을 다시 집어 영원히 돈다
            log.error("Outbox publish is not wired for transType: {} (outbox id: {})",
                    transType, outboxMessage.getId());
            outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
        }

    }

}
