package com.brunosong.deployment.system.deployment.service.messaging.listener.kafka;

import com.brunosong.deployment.contract.ApplyResult;
import com.brunosong.deployment.system.deployment.service.messaging.mapper.DeploymentMessagingDataMapper;
import com.brunosong.deployment.system.deployment.service.ports.input.message.listener.ApplyResultMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplyResultKafkaListener {

    private final ApplyResultMessageListener applyResultMessageListener;
    private final DeploymentMessagingDataMapper deploymentMessagingDataMapper;

    @KafkaListener(topics = "${deployment-service.data-migration-response-topic-name}")
    public void receive(@Payload List<ApplyResult> message,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        // 실패한 위치를 알려 줘야 그 앞은 커밋되고 이 건부터 다시 시도된다.
        // 그냥 던지면 배치 전체가 재시도되고, 소진 후에는 배치 전체가 건너뛰어진다
        for (int index = 0; index < message.size(); index++) {
            ApplyResult result = message.get(index);
            try {
                applyResultMessageListener.deploymentStatusUpdate(
                        deploymentMessagingDataMapper.toApplyResultMessage(result));
            } catch (RuntimeException e) {
                throw new BatchListenerFailedException(
                        "반영 결과 처리에 실패했습니다. deploymentId=" + result.getDeploymentId(), e, index);
            }
        }

    }
}
