package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.outbox;

import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.DataTransferRequestMessagePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * 아웃박스에 쌓인 전송 요청을 카프카로 내보내는 어댑터.
 *
 * 스케줄러가 행을 집어 이 포트를 부르고, 결과는 outboxCallback 으로 되돌려준다.
 * 보내기 전에 상태를 바꾸지 않는다. 브로커가 받았다는 응답이 온 뒤에 COMPLETED 로,
 * 실패하면 FAILED 로 적어야 다음 주기에 다시 집어 갈 수 있다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferRequestKafkaMessagePublisher implements DataTransferRequestMessagePublisher {

    private final TransferServiceConfigData transferServiceConfigData;
    private final TransferMessagingDataMapper transferMessagingDataMapper;
    // 부트 자동 설정이 만들어 준 템플릿을 그대로 쓴다.
    // 전에는 이것을 감싼 KafkaProducer 인터페이스와 구현이 따로 있었는데,
    // send 한 줄을 감싸는 것뿐이라 걷어냈다
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(DataTransferOutboxMessage dataTransferOutboxMessage,
                        BiConsumer<DataTransferOutboxMessage, OutboxStatus> outboxCallback) {

        DataTransferEventPayload payload = readPayload(dataTransferOutboxMessage);

        if (payload == null) {
            // 페이로드를 못 읽으면 재시도해도 같은 결과라 바로 실패로 닫는다
            outboxCallback.accept(dataTransferOutboxMessage, OutboxStatus.FAILED);
            return;
        }

        String sagaId = dataTransferOutboxMessage.getSagaId().toString();
        String topicName = transferServiceConfigData.getDataMigrationRequestTopicName();

        try {

            DataMigrationRequestAvroModel dataMigrationRequestAvroModel =
                    transferMessagingDataMapper.toDataMigrationRequestAvroModel(payload,
                            dataTransferOutboxMessage.getSagaId());

            kafkaTemplate.send(topicName, sagaId, dataMigrationRequestAvroModel)
                    .whenComplete(getKafkaCallback(topicName,
                                                   dataMigrationRequestAvroModel,
                                                   dataTransferOutboxMessage,
                                                   outboxCallback,
                                      "DataMigrationRequestAvroModel"));

            log.info("DataMigrationRequestAvroModel sent to kafka for saga id: {}", sagaId);
        } catch (Exception e) {
            log.error("Error while sending DataMigrationRequestAvroModel message" +
                            " to kafka with saga id: {} and error: {}",
                    sagaId, e.getMessage());
            outboxCallback.accept(dataTransferOutboxMessage, OutboxStatus.FAILED);
        }
    }

    private DataTransferEventPayload readPayload(DataTransferOutboxMessage dataTransferOutboxMessage) {
        try {
            return objectMapper.readValue(dataTransferOutboxMessage.getPayload(), DataTransferEventPayload.class);
        } catch (JsonProcessingException e) {
            log.error("Could not read DataTransferEventPayload for outbox id: {}",
                    dataTransferOutboxMessage.getId(), e);
            return null;
        }
    }

    /**
     * 전송 결과를 아웃박스 상태로 옮기는 콜백.
     *
     * 결과에 따라 아웃박스 행까지 바꿔야 해서 콜백을 직접 만든다.
     */
    private BiConsumer<SendResult<String, Object>, Throwable> getKafkaCallback(
            String responseTopicName,
            DataMigrationRequestAvroModel avroModel,
            DataTransferOutboxMessage dataTransferOutboxMessage,
            BiConsumer<DataTransferOutboxMessage, OutboxStatus> outboxCallback,
            String avroModelName) {

        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} with message: {} and topic {}",
                        avroModelName, avroModel.toString(), responseTopicName, ex);
                outboxCallback.accept(dataTransferOutboxMessage, OutboxStatus.FAILED);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for saga id: {}" +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        dataTransferOutboxMessage.getSagaId(),
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                outboxCallback.accept(dataTransferOutboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }
}
