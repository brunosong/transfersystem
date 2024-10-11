package com.brunosong.transfer.system.aiservice.config.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AiServiceConsumerConfig {


    /* 매뉴얼 커밋 설정 */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> manualCommitFactory(ConsumerFactory<String,Object> consumerFactory, RecordMessageConverter converter) {

        Map<String, Object> props = new HashMap<>(consumerFactory.getConfigurationProperties());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1); // 한건에 리스트를 보낼것이고 리스트에 시간이 많이 걸리기에 한건만 가져온다
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);   // 메시지를 처리할 최대 시간 (10분)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setConcurrency(1);
        factory.setMessageConverter(converter);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;

    }


}
