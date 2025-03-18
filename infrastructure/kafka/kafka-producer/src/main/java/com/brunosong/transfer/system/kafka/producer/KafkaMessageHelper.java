package com.brunosong.transfer.system.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    public <V> BiConsumer<SendResult<String, V>, Throwable> getKafkaCallback(String responseTopicName, V avroModel,
                                                                           String id, String avroModelName) {
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} with message: {} and topic {}",
                        avroModelName, avroModel.toString(), responseTopicName, ex);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for material id: {}" +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        id,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                log.debug("Message sent successfully to topic={}, offset={}",
                        responseTopicName, result.getRecordMetadata().offset());
            }
        };
    }
}
