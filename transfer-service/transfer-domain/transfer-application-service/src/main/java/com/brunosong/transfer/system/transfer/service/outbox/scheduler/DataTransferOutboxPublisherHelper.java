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

//        // 2. 전송할 데이터 설정 정보 찾기
//        if (transfer.getTransType() == TransType.API) {
//            transferApiSendHelper.transferAction(transfer);
//            log.info("API transfer completed: {}", transfer.getId());
//        } else if (transfer.getTransType() == TransType.MESSAGING) {
//            messagingSendHelper.transferAction(transfer);
//            log.info("Messaging transfer completed: {}", transfer.getId());
//        } else {
//            transfer.markFailed();
//            log.warn("Unknown transfer type for: {}", transfer.getId());
//        }

    }

}
