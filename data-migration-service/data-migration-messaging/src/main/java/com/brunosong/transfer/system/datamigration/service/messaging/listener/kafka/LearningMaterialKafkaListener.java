package com.brunosong.transfer.system.datamigration.service.messaging.listener.kafka;

import com.brunosong.transfer.system.datamigration.service.messaging.mapper.LoadTargetMessagingDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.LearningMaterialMessageListener;
import com.brunosong.transfer.system.kafka.consumer.KafkaConsumer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LearningMaterialKafkaListener implements KafkaConsumer<DataMigrationRequestAvroModel> {

    private final LearningMaterialMessageListener learningMaterialMessageListener;
    private final LoadTargetMessagingDataMapper loadTargetMessagingDataMapper;

    public LearningMaterialKafkaListener(LearningMaterialMessageListener learningMaterialMessageListener,
                                         LoadTargetMessagingDataMapper loadTargetMessagingDataMapper) {
        this.learningMaterialMessageListener = learningMaterialMessageListener;
        this.loadTargetMessagingDataMapper = loadTargetMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.data-migration-group-id}", topics = "${data-migration-service.material-request-topic}")
    public void receive(@Payload List<DataMigrationRequestAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        message.forEach(dataMigrationRequestAvroModel -> {
            learningMaterialMessageListener.migration(
                    loadTargetMessagingDataMapper.dataMigrationRequestAvroModelToLearningMaterial(dataMigrationRequestAvroModel)
            );
        });

    }


}
