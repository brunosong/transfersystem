package com.brunosong.transfer.system.transfer.service.ports.output.message.publisher;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;

import java.util.function.BiConsumer;

public interface DataTransferRequestMessagePublisher {

    void publish(DataTransferOutboxMessage dataTransferOutboxMessage,
                 BiConsumer<DataTransferOutboxMessage, OutboxStatus> outboxCallback);
}
