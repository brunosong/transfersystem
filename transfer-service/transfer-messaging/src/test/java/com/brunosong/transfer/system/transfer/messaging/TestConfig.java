package com.brunosong.transfer.system.transfer.messaging;

import com.brunosong.transfer.system.kafka.producer.KafkaMessageHelper;
import com.brunosong.transfer.system.kafka.producer.service.KafkaProducer;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.config.TransferServiceConfigData;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public TransferMessagingDataMapper transferMessagingDataMapper() {
        return Mockito.mock(TransferMessagingDataMapper.class);
    }

    @Bean
    public KafkaProducer<String, DataMigrationRequestAvroModel> kafkaProducer() {
        return Mockito.mock(KafkaProducer.class);
    }

    @Bean
    public TransferServiceConfigData transferServiceConfigData() {
        TransferServiceConfigData transferServiceConfigData = new TransferServiceConfigData();
        transferServiceConfigData.setMaterialRequestTopicName("testTopic");
        return transferServiceConfigData;
    }

    @Bean
    public KafkaMessageHelper kafkaMessageHelper() {
        return Mockito.mock(KafkaMessageHelper.class);
    }
}
