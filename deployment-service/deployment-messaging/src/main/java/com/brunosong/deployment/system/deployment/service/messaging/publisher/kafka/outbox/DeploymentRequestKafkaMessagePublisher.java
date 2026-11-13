package com.brunosong.deployment.system.deployment.service.messaging.publisher.kafka.outbox;

import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.messaging.mapper.DeploymentMessagingDataMapper;
import com.brunosong.deployment.system.deployment.service.config.DeploymentServiceConfigData;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.DeploymentRequestMessagePublisher;
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
 * 아웃박스에 쌓인 배포 요청을 카프카로 내보내는 어댑터.
 *
 * 스케줄러가 행을 집어 이 포트를 부르고, 결과는 outboxCallback 으로 되돌려준다.
 * 보내기 전에 상태를 바꾸지 않는다. 브로커가 받았다는 응답이 온 뒤에 COMPLETED 로,
 * 실패하면 FAILED 로 적어야 다음 주기에 다시 집어 갈 수 있다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentRequestKafkaMessagePublisher implements DeploymentRequestMessagePublisher {

    private final DeploymentServiceConfigData deploymentServiceConfigData;
    private final DeploymentMessagingDataMapper deploymentMessagingDataMapper;
    // 부트 자동 설정이 만들어 준 템플릿을 그대로 쓴다.
    // 전에는 이것을 감싼 KafkaProducer 인터페이스와 구현이 따로 있었는데,
    // send 한 줄을 감싸는 것뿐이라 걷어냈다
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(DeploymentOutboxMessage deploymentOutboxMessage,
                        BiConsumer<DeploymentOutboxMessage, OutboxStatus> outboxCallback) {

        DeploymentEventPayload payload = readPayload(deploymentOutboxMessage);

        if (payload == null) {
            // 페이로드를 못 읽으면 재시도해도 같은 결과라 바로 실패로 닫는다
            outboxCallback.accept(deploymentOutboxMessage, OutboxStatus.FAILED);
            return;
        }

        String id = deploymentOutboxMessage.getId().toString();
        String topicName = deploymentServiceConfigData.getDataMigrationRequestTopicName();

        try {

            ApplyChunkCommand command = deploymentMessagingDataMapper.toApplyChunkCommand(payload);

            // 키는 행을 넣을 때 정했다. 같은 커리큘럼 × 대상은 같은 파티션으로 간다
            kafkaTemplate.send(topicName, deploymentOutboxMessage.getMessageKey(), command)
                    .whenComplete(getKafkaCallback(topicName,
                                                   command,
                                                   deploymentOutboxMessage,
                                                   outboxCallback,
                                      "ApplyChunkCommand"));

            log.info("ApplyChunkCommand sent to kafka for outbox id: {}", id);
        } catch (Exception e) {
            log.error("Error while sending ApplyChunkCommand message" +
                            " to kafka with outbox id: {} and error: {}",
                    id, e.getMessage());
            outboxCallback.accept(deploymentOutboxMessage, OutboxStatus.FAILED);
        }
    }

    private DeploymentEventPayload readPayload(DeploymentOutboxMessage deploymentOutboxMessage) {
        try {
            return objectMapper.readValue(deploymentOutboxMessage.getPayload(), DeploymentEventPayload.class);
        } catch (JsonProcessingException e) {
            log.error("Could not read DeploymentEventPayload for outbox id: {}",
                    deploymentOutboxMessage.getId(), e);
            return null;
        }
    }

    /**
     * 발행 결과를 아웃박스 상태로 옮기는 콜백.
     *
     * 결과에 따라 아웃박스 행까지 바꿔야 해서 콜백을 직접 만든다.
     */
    private BiConsumer<SendResult<String, Object>, Throwable> getKafkaCallback(
            String responseTopicName,
            ApplyChunkCommand avroModel,
            DeploymentOutboxMessage deploymentOutboxMessage,
            BiConsumer<DeploymentOutboxMessage, OutboxStatus> outboxCallback,
            String avroModelName) {

        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} with message: {} and topic {}",
                        avroModelName, avroModel.toString(), responseTopicName, ex);
                outboxCallback.accept(deploymentOutboxMessage, OutboxStatus.FAILED);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for outbox id: {}" +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        deploymentOutboxMessage.getId(),
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                outboxCallback.accept(deploymentOutboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }
}
