package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferDataSendKafkaMessagePublisher implements TransferDataSendMessagePublisher {

    private final TransferServiceConfigData transferServiceConfigData;
    private final TransferMessagingDataMapper transferMessagingDataMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Transfer transfer) {

        DataMigrationRequestAvroModel dataMigrationRequestAvroModel =
                transferMessagingDataMapper.toDataMigrationRequestAvroModel(transfer);

        String sagaId = dataMigrationRequestAvroModel.getSagaId();
        String topicName = transferServiceConfigData.getDataMigrationRequestTopicName();

        kafkaTemplate.send(topicName, sagaId, dataMigrationRequestAvroModel)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("DataMigrationRequestAvroModel 발행 실패. sagaId={}", sagaId, ex);
                    } else {
                        log.info("DataMigrationRequestAvroModel sent to kafka for saga id: {}", sagaId);
                    }
                });
    }
}
