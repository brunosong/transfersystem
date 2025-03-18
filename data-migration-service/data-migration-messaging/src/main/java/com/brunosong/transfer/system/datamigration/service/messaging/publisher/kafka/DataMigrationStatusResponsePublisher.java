package com.brunosong.transfer.system.datamigration.service.messaging.publisher.kafka;

import com.brunosong.transfer.system.datamigration.service.config.DataMigrationServiceConfigData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationStatusOutboxMessage;
import com.brunosong.transfer.system.datamigration.service.messaging.mapper.DataMigrationMessagingDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.kafka.producer.KafkaMessageHelper;
import com.brunosong.transfer.system.kafka.producer.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataMigrationStatusResponsePublisher implements DataMigrationResponsePublisher {

    private final KafkaProducer<String, DataMigrationResponseAvroModel> kafkaProducer;
    private final DataMigrationMessagingDataMapper dataMigrationMessagingDataMapper;
    private final DataMigrationServiceConfigData dataMigrationServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void dataMigrationStatusPublish(DataMigrationStatusOutboxMessage outboxMessage) {

        DataMigrationResponseAvroModel responseAvroModel = dataMigrationMessagingDataMapper
                .toDataMigrationResponseAvroModel(outboxMessage);

        try {

            kafkaProducer.send(dataMigrationServiceConfigData.getDataMigrationResponseTopicName(),
                                responseAvroModel.getTransferId(),
                                responseAvroModel,
                                kafkaMessageHelper.getKafkaCallback(dataMigrationServiceConfigData.getDataMigrationResponseTopicName(),
                                                                    responseAvroModel,
                                                                    responseAvroModel.getTransferId(),
                                                        "DataMigrationResponseAvroModel")
            );

            log.info("DataMigrationResponseAvroModel sent to kafka for transferId id: {}", responseAvroModel.getTransferId());
        } catch (Exception e) {
            log.error("Error while sending DataMigrationResponseAvroModel message" +
                            " to kafka with transferId id: {} and error: {}",
                    responseAvroModel.getTransferId(), e.getMessage());
        }
    }
}
