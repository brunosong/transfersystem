package com.brunosong.deployment.system.deployment.service.messaging.publisher.kafka.outbox;

import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

// 페이로드를 못 읽을 때의 테스트는 DeploymentOutboxPublisherHelperTest 로 옮겼다. 이제 페이로드는 그쪽에서 읽는다
class DeploymentRequestKafkaMessagePublisherTest {

    @Test
    @DisplayName("메시지가 너무 크면 감싸여 와도 포기하고, 그 밖의 Kafka 오류는 다음에 다시 보낸다")
    void classifyKafkaErrors() {
        assertThat(DeploymentRequestKafkaMessagePublisher.classify(new KafkaException(new RecordTooLargeException("too large"))))
                .isEqualTo(PublishResult.GIVE_UP);
        assertThat(DeploymentRequestKafkaMessagePublisher.classify(new TimeoutException("no metadata")))
                .isEqualTo(PublishResult.RETRY_LATER);
    }

}
