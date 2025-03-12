package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.kafka.producer.KafkaMessageHelper;
import com.brunosong.transfer.system.kafka.producer.service.KafkaProducer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferDataSendKafkaMessagePublisher implements TransferDataSendMessagePublisher {

    private final TransferServiceConfigData transferServiceConfigData;
    private final TransferMessagingDataMapper transferMessagingDataMapper;
    private final KafkaProducer<String, DataMigrationRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper kafkaMessageHelper;

    public TransferDataSendKafkaMessagePublisher(TransferMessagingDataMapper transferMessagingDataMapper,
                                                 KafkaProducer<String, DataMigrationRequestAvroModel> kafkaProducer,
                                                 TransferServiceConfigData transferServiceConfigData,
                                                 KafkaMessageHelper kafkaMessageHelper) {
        this.transferMessagingDataMapper = transferMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.transferServiceConfigData = transferServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(Transfer transfer, SourceContentData sourceContentData) {

        DataMigrationRequestAvroModel dataMigrationRequestAvroModel =
                transferMessagingDataMapper.toDataMigrationRequestAvroModel(transfer, sourceContentData);

        String sagaId = dataMigrationRequestAvroModel.getSagaId();
        String transferId = dataMigrationRequestAvroModel.getTransferId();
        
        kafkaProducer.send(transferServiceConfigData.getDataMigrationRequestTopicName(),
                            sagaId,
                            dataMigrationRequestAvroModel,
                            kafkaMessageHelper.getKafkaCallback(transferServiceConfigData.getDataMigrationRequestTopicName(),
                                                                dataMigrationRequestAvroModel,
                                                                transferId,
                                                   "DataMigrationRequestAvroModel")
                            );

    }
}
