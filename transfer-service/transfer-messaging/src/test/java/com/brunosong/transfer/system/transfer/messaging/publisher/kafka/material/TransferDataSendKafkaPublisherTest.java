package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.kafka.producer.KafkaMessageHelper;
import com.brunosong.transfer.system.kafka.producer.service.KafkaProducer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.transfer.messaging.TestConfig;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = { TestConfig.class, TransferDataSendKafkaPublisher.class})
class TransferDataSendKafkaPublisherTest {

    @Autowired
    public TransferDataSendKafkaPublisher sut;

    @Autowired
    public KafkaProducer<String, LearningMaterialAvroModel> kafkaProducer;

    @Autowired
    public TransferServiceConfigData transferServiceConfigData;

    @Autowired
    public KafkaMessageHelper kafkaMessageHelper;

    @Autowired
    public TransferMessagingDataMapper transferMessagingDataMapper;


    @Test
    @DisplayName("send 메소드가 정상적으로 실행된다.")
    void publish_1() {
        // given
        LearningMaterialAvroModel avroModel = LearningMaterialAvroModel.newBuilder()
                .setId("ABCD1234")
                .setTitle("테스트")
                .setDescription("설명")
                .setMetadataList(List.of())
                .build();

        LearningMaterial learningMaterial = LearningMaterial.builder()
                .id(new LearningMaterialId("ABCD1234"))
                .build();

        // when
        when(transferMessagingDataMapper.learningMaterialToLearningMaterialAvroModel(learningMaterial)).thenReturn(avroModel);
        sut.publish(null, learningMaterial);

        // then
        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(LearningMaterialAvroModel.class), any());

    }


    @Test
    @DisplayName("send 메소드에 원하는 값이 넘어온다.")
    void publish_2() {
        // given
        LearningMaterialAvroModel avroModel = LearningMaterialAvroModel.newBuilder()
                .setId("ABCD1234")
                .setTitle("테스트")
                .setDescription("설명")
                .setMetadataList(List.of())
                .build();

        LearningMaterial learningMaterial = LearningMaterial.builder()
                .id(new LearningMaterialId("ABCD1234"))
                .build();

        // capture
        ArgumentCaptor<String> topicNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LearningMaterialAvroModel> avroModelCaptor = ArgumentCaptor.forClass(LearningMaterialAvroModel.class);


        // when
        when(transferMessagingDataMapper.learningMaterialToLearningMaterialAvroModel(learningMaterial)).thenReturn(avroModel);
        sut.publish(null, learningMaterial);


        // then
        verify(kafkaProducer).send(topicNameCaptor.capture(),keyCaptor.capture(), avroModelCaptor.capture(), any());

        Assertions.assertThat(topicNameCaptor.getValue()).isEqualTo(transferServiceConfigData.getMaterialRequestTopicName());
        Assertions.assertThat(keyCaptor.getValue()).isEqualTo(avroModel.getId());
        Assertions.assertThat(avroModelCaptor.getValue()).isEqualTo(avroModel);
    }
}