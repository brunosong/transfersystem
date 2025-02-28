package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.kafka.producer.KafkaMessageHelper;
import com.brunosong.transfer.system.kafka.producer.service.KafkaProducer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferDataSendKafkaPublisher implements TransferDataSendPublisher {

    private final TransferServiceConfigData transferServiceConfigData;
    private final TransferMessagingDataMapper transferMessagingDataMapper;
    private final KafkaProducer<String, DataMigrationRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper kafkaMessageHelper;

    public TransferDataSendKafkaPublisher(TransferMessagingDataMapper transferMessagingDataMapper,
                                          KafkaProducer<String, DataMigrationRequestAvroModel> kafkaProducer,
                                          TransferServiceConfigData transferServiceConfigData,
                                          KafkaMessageHelper kafkaMessageHelper) {
        this.transferMessagingDataMapper = transferMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.transferServiceConfigData = transferServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(Transfer transfer, LearningMaterial learningMaterial) {

        DataMigrationRequestAvroModel dataMigrationRequestAvroModel =
                transferMessagingDataMapper.toLearningMaterialAvroModel(transfer, learningMaterial);

        String learningMaterialId = dataMigrationRequestAvroModel.getId();

        kafkaProducer.send(transferServiceConfigData.getMaterialRequestTopicName(),
                            learningMaterialId,
                            dataMigrationRequestAvroModel,
                            kafkaMessageHelper.getKafkaCallback(transferServiceConfigData.getMaterialRequestTopicName(),
                                                                dataMigrationRequestAvroModel,
                                                                learningMaterialId,
                                                   "DataMigrationRequestAvroModel")
                            );

    }
}
