package com.brunosong.deployment.system.deployment.service.messaging.publisher.kafka.outbox;

import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import com.brunosong.deployment.system.deployment.service.messaging.mapper.DeploymentMessagingDataMapper;
import com.brunosong.deployment.system.deployment.service.config.DeploymentServiceConfigData;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.DeploymentRequestMessagePublisher;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 아웃박스에 쌓인 배포 요청을 카프카로 내보내는 어댑터.
 *
 * 행마다 브로커가 받았다는 응답(acks=all)을 기다린 뒤 결과를 돌려준다. 응답을 받아야 행을 닫을 수 있고,
 * 앞 행의 결과를 알아야 같은 키의 뒤 행을 보낼지 정할 수 있다.
 * 어떤 실패를 다시 보낼지도 여기서 정한다. 스케줄러는 Kafka 예외를 모른다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentRequestKafkaMessagePublisher implements DeploymentRequestMessagePublisher {

    /** 행 하나에 브로커 응답을 기다리는 시간. 지나면 다음 주기에 다시 보낸다 */
    private static final long SEND_TIMEOUT_MILLIS = 10_000;

    private final DeploymentServiceConfigData deploymentServiceConfigData;
    private final DeploymentMessagingDataMapper deploymentMessagingDataMapper;
    // 부트 자동 설정이 만들어 준 템플릿을 그대로 쓴다.
    // 전에는 이것을 감싼 KafkaProducer 인터페이스와 구현이 따로 있었는데,
    // send 한 줄을 감싸는 것뿐이라 걷어냈다
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public PublishResult publish(DeploymentOutboxMessage deploymentOutboxMessage, DeploymentEventPayload payload,
                                 ChunkContent content) {

        ApplyChunkCommand command;
        try {
            command = deploymentMessagingDataMapper.toApplyChunkCommand(payload, content);
        } catch (RuntimeException e) {
            // 계약 모양으로 못 바꾸면 다시 해도 같다
            log.error("ApplyChunkCommand 로 바꾸지 못했다. outbox id: {}", deploymentOutboxMessage.getId(), e);
            return PublishResult.GIVE_UP;
        }

        String topicName = deploymentServiceConfigData.getDataMigrationRequestTopicName();
        try {
            // 키는 행을 넣을 때 정했다. 같은 커리큘럼 × 대상은 같은 파티션으로 간다
            RecordMetadata metadata = kafkaTemplate.send(topicName, deploymentOutboxMessage.getMessageKey(), command)
                    .get(SEND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .getRecordMetadata();
            log.info("ApplyChunkCommand 를 보냈다. outbox id: {}, partition: {}, offset: {}",
                    deploymentOutboxMessage.getId(), metadata.partition(), metadata.offset());
            return PublishResult.PUBLISHED;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PublishResult.RETRY_LATER;
        } catch (TimeoutException e) {
            log.warn("브로커 응답이 {}ms 안에 오지 않았다. 다음 주기에 다시 보낸다. outbox id: {}",
                    SEND_TIMEOUT_MILLIS, deploymentOutboxMessage.getId());
            return PublishResult.RETRY_LATER;
        } catch (ExecutionException e) {
            // 브로커가 거절했거나 프로듀서가 재시도 끝에 포기했다
            log.warn("ApplyChunkCommand 를 보내지 못했다. outbox id: {}", deploymentOutboxMessage.getId(), e.getCause());
            return classify(e.getCause());
        } catch (RuntimeException e) {
            // send() 가 바로 던졌다(직렬화 실패, 메타데이터를 못 받음 등)
            log.warn("ApplyChunkCommand 를 보내지 못했다. outbox id: {}", deploymentOutboxMessage.getId(), e);
            return classify(e);
        }
    }

    /**
     * 메시지가 너무 크면 다시 보내도 소용없다. 그 밖의 Kafka 오류는 브로커나 스키마 레지스트리가
     * 돌아오면 나갈 수 있으므로 다시 보낸다. 예외가 감싸여 올 수 있어 원인을 끝까지 본다.
     */
    static PublishResult classify(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof RecordTooLargeException) {
                return PublishResult.GIVE_UP;
            }
        }
        return PublishResult.RETRY_LATER;
    }

}
