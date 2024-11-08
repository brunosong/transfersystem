package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.kafka.producer.KafkaMessageHelper;
import com.brunosong.transfer.system.kafka.producer.service.KafkaProducer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.LearningMaterialRequestPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MaterialSendingPublisher implements LearningMaterialRequestPublisher {

    private final TransferMessagingDataMapper transferMessagingDataMapper;
    private final KafkaProducer<String, LearningMaterialAvroModel> kafkaProducer;
    private final TransferServiceConfigData transferServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public MaterialSendingPublisher(TransferMessagingDataMapper transferMessagingDataMapper,
                                    KafkaProducer<String, LearningMaterialAvroModel> kafkaProducer,
                                    TransferServiceConfigData transferServiceConfigData,
                                    KafkaMessageHelper kafkaMessageHelper) {
        this.transferMessagingDataMapper = transferMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.transferServiceConfigData = transferServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(LearningMaterial learningMaterial) {

        LearningMaterialAvroModel learningMaterialAvroModel =
                transferMessagingDataMapper.learningMaterialToLearningMaterialAvroModel(learningMaterial);

        String learningMaterialId = learningMaterialAvroModel.getId();

        kafkaProducer.send(transferServiceConfigData.getMaterialRequestTopicName(),
                            learningMaterialId,
                            learningMaterialAvroModel,
                            kafkaMessageHelper.getKafkaCallback(transferServiceConfigData.getMaterialRequestTopicName(),
                                                                learningMaterialAvroModel,
                                                                learningMaterialId,
                                                                "LearningMaterialAvroModel")
                            );

    }
}
