package com.brunosong.transfer.system.datamigration.service.messaging.publisher.kafka;

import com.brunosong.transfer.system.datamigration.service.config.DataMigrationServiceConfigData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationResponseMessage;
import com.brunosong.transfer.system.datamigration.service.messaging.mapper.DataMigrationMessagingDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
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
    public void dataMigrationStatusPublish(DataMigrationResponseMessage responseMessage) {

        DataMigrationResponseAvroModel responseAvroModel = dataMigrationMessagingDataMapper
                .toDataMigrationResponseAvroModel(responseMessage);

        String topicName = dataMigrationServiceConfigData.getDataMigrationResponseTopicName();

        kafkaTemplate.send(topicName, responseAvroModel.getTransferId(), responseAvroModel)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("DataMigrationResponseAvroModel 발행 실패. transferId={}",
                                responseAvroModel.getTransferId(), ex);
                    } else {
                        log.info("DataMigrationResponseAvroModel sent to kafka for transferId id: {}",
                                responseAvroModel.getTransferId());
                    }
                });
    }
}
