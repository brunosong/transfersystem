package com.brunosong.deployment.system.targetagent.service.messaging.publisher.kafka;

import com.brunosong.deployment.contract.ApplyResult;
import com.brunosong.deployment.system.targetagent.service.config.TargetAgentConfigData;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyResultMessage;
import com.brunosong.deployment.system.targetagent.service.messaging.mapper.TargetAgentMessagingDataMapper;
import com.brunosong.deployment.system.targetagent.service.ports.output.message.publisher.deployment.ApplyResultPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyResultKafkaPublisher implements ApplyResultPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TargetAgentMessagingDataMapper targetAgentMessagingDataMapper;
    private final TargetAgentConfigData targetAgentConfigData;

    @Override
    public void publish(ApplyResultMessage resultMessage) {

        ApplyResult result = targetAgentMessagingDataMapper.toApplyResult(resultMessage);

        String topicName = targetAgentConfigData.getDataMigrationResponseTopicName();

        kafkaTemplate.send(topicName, result.getDeploymentId(), result)
                .whenComplete((sendResult, ex) -> {
                    if (ex != null) {
                        log.error("ApplyResult 발행 실패. deploymentId={}, chunkNo={}",
                                result.getDeploymentId(), result.getChunkNo(), ex);
                    } else {
                        log.info("ApplyResult 발행. deploymentId={}, chunkNo={}",
                                result.getDeploymentId(), result.getChunkNo());
                    }
                });
    }
}
