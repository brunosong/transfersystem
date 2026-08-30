package com.brunosong.transfer.system.datamigration.service.messaging.listener.kafka;

import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.messaging.mapper.DataMigrationMessagingDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.kafka.consumer.KafkaConsumer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DataMigrationRequestKafkaListener implements KafkaConsumer<DataMigrationRequestAvroModel> {

    private final DataMigrationMessageListener dataMigrationMessageListener;
    private final DataMigrationMessagingDataMapper dataMigrationMessagingDataMapper;

    public DataMigrationRequestKafkaListener(DataMigrationMessageListener dataMigrationMessageListener,
                                             DataMigrationMessagingDataMapper dataMigrationMessagingDataMapper) {
        this.dataMigrationMessageListener = dataMigrationMessageListener;
        this.dataMigrationMessagingDataMapper = dataMigrationMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.data-migration-group-id}",
                   topics = "${data-migration-service.data-migration-request-topic-name}",
                   containerFactory = "dataMigrationListenerContainerFactory")
    public void receive(@Payload List<DataMigrationRequestAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        // forEach 로 돌면 한 건이 실패했을 때 배치 전체가 다시 처리된다.
        // 앞의 건은 중복 처리되고, 재시도를 소진하면 뒤의 건은 통째로 건너뛰어진다.
        // 실패한 위치를 BatchListenerFailedException 에 담아 던지면 그 앞은 커밋되고
        // 이 건부터 다시 시도된다
        for (int index = 0; index < message.size(); index++) {
            DataMigrationRequestAvroModel avroModel = message.get(index);
            try {
                DataMigrationRequest dataMigrationRequest =
                        dataMigrationMessagingDataMapper.avroModelToDataMigrationRequest(avroModel);

                dataMigrationMessageListener.migration(dataMigrationRequest);
            } catch (RuntimeException e) {
                throw new BatchListenerFailedException(
                        "이관 요청 처리에 실패했습니다. transferId=" + avroModel.getTransferId(), e, index);
            }
        }
    }

}
