package com.brunosong.deployment.system.datamigration.service.messaging.publisher.kafka;

import com.brunosong.deployment.contract.ApplyResult;
import com.brunosong.deployment.system.datamigration.service.config.DataMigrationServiceConfigData;
import com.brunosong.deployment.system.datamigration.service.dto.message.ApplyResultMessage;
import com.brunosong.deployment.system.datamigration.service.messaging.mapper.DataMigrationMessagingDataMapper;
import com.brunosong.deployment.system.datamigration.service.ports.output.message.publisher.deployment.DataMigrationResponsePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataMigrationStatusResponsePublisher implements DataMigrationResponsePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DataMigrationMessagingDataMapper dataMigrationMessagingDataMapper;
    private final DataMigrationServiceConfigData dataMigrationServiceConfigData;

    @Override
    public void dataMigrationStatusPublish(ApplyResultMessage resultMessage) {

        ApplyResult result = dataMigrationMessagingDataMapper.toApplyResult(resultMessage);

        String topicName = dataMigrationServiceConfigData.getDataMigrationResponseTopicName();

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
