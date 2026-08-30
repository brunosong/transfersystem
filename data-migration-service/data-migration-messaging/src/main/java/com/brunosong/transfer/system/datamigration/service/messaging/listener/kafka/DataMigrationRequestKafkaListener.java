package com.brunosong.transfer.system.datamigration.service.messaging.listener.kafka;

import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.messaging.mapper.DataMigrationMessagingDataMapper;
import com.brunosong.transfer.system.datamigration.service.ports.input.message.listener.DataMigrationMessageListener;
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
public class DataMigrationRequestKafkaListener {

    private final DataMigrationMessageListener dataMigrationMessageListener;
    private final DataMigrationMessagingDataMapper dataMigrationMessagingDataMapper;

    public DataMigrationRequestKafkaListener(DataMigrationMessageListener dataMigrationMessageListener,
                                             DataMigrationMessagingDataMapper dataMigrationMessagingDataMapper) {
        this.dataMigrationMessageListener = dataMigrationMessageListener;
        this.dataMigrationMessagingDataMapper = dataMigrationMessagingDataMapper;
    }

    // 컨테이너 팩토리를 지정하지 않는다. 부트가 만든 기본 팩토리를 쓰고, 거기에
    // kafka-support 의 공통 에러 핸들러가 붙는다. 전에는 서비스가 자기 팩토리를
    // 따로 만들면서 그 정책이 안 붙어 이 리스너만 DLT 가 없었다
    @KafkaListener(topics = "${data-migration-service.data-migration-request-topic-name}")
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
