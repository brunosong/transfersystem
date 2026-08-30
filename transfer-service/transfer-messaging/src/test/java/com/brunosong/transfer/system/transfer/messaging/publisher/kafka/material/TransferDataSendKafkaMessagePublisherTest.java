package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.kafka.transfer.avro.model.SourceContentDataAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 부트가 만들어 준 KafkaTemplate 을 그대로 쓰도록 바꾼 뒤의 배선을 확인한다.
 * 전에는 KafkaProducer 인터페이스를 감싸고 있었고 그 테스트는 통째로 주석이었다.
 */
class TransferDataSendKafkaMessagePublisherTest {

    private final TransferServiceConfigData configData = new TransferServiceConfigData();
    private final TransferMessagingDataMapper mapper = mock(TransferMessagingDataMapper.class);

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

    private TransferDataSendKafkaMessagePublisher sut;

    @BeforeEach
    void setUp() {
        configData.setDataMigrationRequestTopicName("data-migration-request");
        sut = new TransferDataSendKafkaMessagePublisher(configData, mapper, kafkaTemplate);
    }

    @Test
    @DisplayName("설정에 적힌 토픽으로 sagaId 를 키로 삼아 보낸다")
    void publish() {
        DataMigrationRequestAvroModel avroModel = avroModel();

        when(mapper.toDataMigrationRequestAvroModel(any(Transfer.class))).thenReturn(avroModel);
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        sut.publish(Transfer.builder().build());

        // 키가 sagaId 라야 같은 사가의 메시지가 같은 파티션으로 간다
        verify(kafkaTemplate).send("data-migration-request", avroModel.getSagaId(), avroModel);
    }

    private DataMigrationRequestAvroModel avroModel() {
        return DataMigrationRequestAvroModel.newBuilder()
                .setSagaId(UUID.randomUUID().toString())
                .setTransferId(UUID.randomUUID().toString())
                .setDataMigrationId(1L)
                .setSourceContentData(SourceContentDataAvroModel.newBuilder()
                        .setChunkOffset(0)
                        .setSourceId("source-1")
                        .setJsonData(ByteBuffer.wrap(new byte[]{1, 2, 3}))
                        .build())
                .build();
    }
}
