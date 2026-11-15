package com.brunosong.deployment.system.deployment.service.messaging.publisher.kafka.outbox;

import com.brunosong.deployment.system.deployment.service.config.DeploymentServiceConfigData;
import com.brunosong.deployment.system.deployment.service.messaging.mapper.DeploymentMessagingDataMapper;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeploymentRequestKafkaMessagePublisherTest {

    @Test
    @DisplayName("메시지가 너무 크면 감싸여 와도 포기하고, 그 밖의 Kafka 오류는 다음에 다시 보낸다")
    void classifyKafkaErrors() {
        assertThat(DeploymentRequestKafkaMessagePublisher.classify(new KafkaException(new RecordTooLargeException("too large"))))
                .isEqualTo(PublishResult.GIVE_UP);
        assertThat(DeploymentRequestKafkaMessagePublisher.classify(new TimeoutException("no metadata")))
                .isEqualTo(PublishResult.RETRY_LATER);
    }

    @Test
    @DisplayName("페이로드를 못 읽으면 Kafka 로 보내 보지도 않고 포기한다")
    @SuppressWarnings("unchecked")
    void unreadablePayloadGivesUpWithoutSending() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        DeploymentRequestKafkaMessagePublisher publisher = new DeploymentRequestKafkaMessagePublisher(
                mock(DeploymentServiceConfigData.class), mock(DeploymentMessagingDataMapper.class),
                kafkaTemplate, new ObjectMapper());
        DeploymentOutboxMessage message = DeploymentOutboxMessage.builder()
                .id(UUID.randomUUID()).messageKey("ONLINE_CAMPUS:PROD:CUR01").payload("JSON 이 아님").build();

        assertThat(publisher.publish(message)).isEqualTo(PublishResult.GIVE_UP);
        verify(kafkaTemplate, never()).send(any(String.class), any(String.class), any());
    }

}