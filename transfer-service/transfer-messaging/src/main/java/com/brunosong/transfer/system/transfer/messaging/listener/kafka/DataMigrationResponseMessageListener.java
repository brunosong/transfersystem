package com.brunosong.transfer.system.transfer.messaging.listener.kafka;

import com.brunosong.transfer.system.kafka.consumer.KafkaConsumer;
import com.brunosong.transfer.system.kafka.datamigration.avro.model.DataMigrationResponseAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.input.message.listener.DataMigrationMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataMigrationResponseMessageListener implements KafkaConsumer<DataMigrationResponseAvroModel> {

    private final DataMigrationMessageListener dataMigrationMessageListener;
    private final TransferMessagingDataMapper transferMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.transfer-group-id}",
                   topics = "${transfer-service.data-migration-response-topic-name}")
    public void receive(@Payload List<DataMigrationResponseAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        message.forEach( model -> {
            dataMigrationMessageListener.transferStatusUpdate(transferMessagingDataMapper.toDataMigrationResponse(model));
        });

    }
}
