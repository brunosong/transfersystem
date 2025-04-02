package com.brunosong.transfer.system.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface KafkaConsumer<T extends SpecificRecordBase> {
    void receive(List<T> message, List<String> keys, List<Integer> partitions, List<Long> offsets);
}
