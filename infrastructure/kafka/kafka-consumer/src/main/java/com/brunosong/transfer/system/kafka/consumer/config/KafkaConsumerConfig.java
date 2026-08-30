package com.brunosong.transfer.system.kafka.consumer.config;

import com.brunosong.transfer.system.kafka.config.data.KafkaConfigData;
import com.brunosong.transfer.system.kafka.config.data.KafkaConsumerConfigData;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerConfig<K extends Serializable, V extends SpecificRecordBase> {

    /** 실패한 레코드를 보낼 토픽 접미사. 원본 토픽 이름 뒤에 붙는다 */
    private static final String DLT_SUFFIX = ".DLT";

    private final KafkaConfigData kafkaConfigData;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    public KafkaConsumerConfig(KafkaConfigData kafkaConfigData,
                               KafkaConsumerConfigData kafkaConsumerConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaConsumerConfigData = kafkaConsumerConfigData;
    }

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfigData.getKeyDeserializer());

        // 역직렬화를 ErrorHandlingDeserializer 로 감싼다. 설정에 적어 둔 역직렬화기는 그 아래로 들어간다.
        // 이렇게 하지 않으면 깨진 메시지를 만났을 때 poll 단계에서 터지고, 컨테이너가 그 레코드를
        // 건너뛸 방법이 없어 같은 자리에서 무한히 반복한다.
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, kafkaConsumerConfigData.getValueDeserializer());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerConfigData.getAutoOffsetReset());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerConfigData.getSessionTimeoutMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConsumerConfigData.getHeartbeatIntervalMs());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerConfigData.getMaxPollIntervalMs());
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                kafkaConsumerConfigData.getMaxPartitionFetchBytesDefault() * kafkaConsumerConfigData.getMaxPartitionFetchBytesBoostFactor());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerConfigData.getMaxPollRecords());

        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, kafkaConsumerConfigData.getFetchMaxWaitMs());
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, kafkaConsumerConfigData.getFetchMinBytes());

        props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
        props.put(kafkaConsumerConfigData.getSpecificAvroReaderKey(), kafkaConsumerConfigData.getSpecificAvroReader());


        return props;
    }

    @Bean
    public ConsumerFactory<K,V> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    /**
     * 처리에 실패한 레코드를 어떻게 할지 정한다.
     *
     * 재시도는 간격을 두고 30초까지만 한다. 간격 없이 반복하면 상대가 잠깐 죽은 경우에도
     * 순식간에 소진된다. 그래도 안 되면 원본 토픽 이름 뒤에 .DLT 를 붙인 토픽으로 보낸다.
     * 보내고 나면 오프셋이 넘어가므로 뒤에 있는 정상 메시지가 막히지 않는다.
     *
     * 발행용 템플릿이 없는 컨텍스트에서는 로그만 남긴다. 그 경우 실패한 레코드는 사라지므로
     * 기동 로그에서 어느 쪽으로 붙었는지 확인해야 한다.
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler(ObjectProvider<KafkaOperations<?, ?>> kafkaOperationsProvider) {
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(30_000L);

        KafkaOperations<?, ?> kafkaOperations = kafkaOperationsProvider.getIfAvailable();
        ConsumerRecordRecoverer recoverer;

        if (kafkaOperations == null) {
            log.warn("KafkaOperations 빈이 없어 DLT 발행을 붙이지 못했습니다. 실패한 레코드는 로그만 남고 버려집니다");
            recoverer = (record, exception) ->
                    log.error("레코드 처리에 실패했습니다. topic={} partition={} offset={}",
                            record.topic(), record.partition(), record.offset(), exception);
        } else {
            log.info("실패한 레코드를 {} 토픽으로 보냅니다", "<원본토픽>" + DLT_SUFFIX);
            // 파티션을 -1 로 두어 DLT 토픽의 파티션 수가 원본과 달라도 보낼 수 있게 한다.
            // 기본값은 원본과 같은 파티션 번호라, DLT 파티션이 더 적으면 발행이 실패한다
            recoverer = new DeadLetterPublishingRecoverer(kafkaOperations,
                    (record, exception) -> new TopicPartition(record.topic() + DLT_SUFFIX, -1));
        }

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // 역직렬화 실패는 몇 번을 다시 해도 결과가 같다. 재시도하지 않고 바로 DLT 로 보낸다
        errorHandler.addNotRetryableExceptions(
                org.springframework.kafka.support.serializer.DeserializationException.class,
                org.springframework.messaging.converter.MessageConversionException.class,
                java.lang.ClassCastException.class);

        return errorHandler;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K,V>> kafkaListenerContainerFactory(
            CommonErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<K,V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(kafkaConsumerConfigData.getBatchListener());
        factory.setConcurrency(kafkaConsumerConfigData.getConcurrencyLevel());
        factory.setAutoStartup(kafkaConsumerConfigData.getAutoStartup());
        factory.getContainerProperties().setPollTimeout(kafkaConsumerConfigData.getPollTimeoutMs());
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }


}
