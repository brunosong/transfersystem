package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.kafka.config.data.KafkaConsumerConfigData;
import com.brunosong.transfer.system.kafka.transfer.avro.model.DataMigrationRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class BeanConfiguration {

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, DataMigrationRequestAvroModel>> dataMigrationListenerContainerFactory (
            ConsumerFactory consumerFactory,
            KafkaConsumerConfigData kafkaConsumerConfigData
    ) {
        ConcurrentKafkaListenerContainerFactory<String, DataMigrationRequestAvroModel> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(kafkaConsumerConfigData.getBatchListener());
        factory.setConcurrency(kafkaConsumerConfigData.getConcurrencyLevel());
        factory.setAutoStartup(kafkaConsumerConfigData.getAutoStartup());
        factory.getContainerProperties().setPollTimeout(kafkaConsumerConfigData.getPollTimeoutMs());
        factory.setCommonErrorHandler(customErrorHandler());
        return factory;
    }

    // DLT 처리 방향 고민중
    @Bean
    public CommonErrorHandler customErrorHandler() {
        return new DefaultErrorHandler(((consumerRecord, e) -> {
            log.error("[Error] topic = {}, key = {},  error message = {}",
                    consumerRecord.topic(), consumerRecord.key(), e.getMessage());

        }) , new FixedBackOff(3000L, 3));
    }



}
