package com.brunosong.transfer.system.datamigration.service.messaging.listener.kafka;

import com.brunosong.transfer.system.kafka.consumer.KafkaConsumer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.datamigration.service.messaging.mapper.LoadTargetMessagingDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.LearningMaterialMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LearningMaterialKafkaListener implements KafkaConsumer<LearningMaterialAvroModel> {

    private final LearningMaterialMessageListener learningMaterialMessageListener;
    private final LoadTargetMessagingDataMapper loadTargetMessagingDataMapper;

    public LearningMaterialKafkaListener(LearningMaterialMessageListener learningMaterialMessageListener,
                                         LoadTargetMessagingDataMapper loadTargetMessagingDataMapper) {
        this.learningMaterialMessageListener = learningMaterialMessageListener;
        this.loadTargetMessagingDataMapper = loadTargetMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${}", topics = "${}")
    public void receive(@Payload List<LearningMaterialAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {


        message.forEach(learningMaterialAvroModel -> {
            learningMaterialMessageListener.load(
                    loadTargetMessagingDataMapper.learningMaterialAvroModelToLearningMaterial(learningMaterialAvroModel)
            );
        });

    }


}
