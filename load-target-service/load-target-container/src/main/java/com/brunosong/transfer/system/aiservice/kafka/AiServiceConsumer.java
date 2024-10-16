package com.brunosong.transfer.system.aiservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/* 이렇게 ai_service_topic 을 만들어서 한 토픽에 여러 테이블 정보를 담은 메시지를 만들어 순서대로 처리하게 하여도 된다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiServiceConsumer {
//
//    private final AiChapService chapService;
//    private final ChapMapper chapMapper;
//
//    @KafkaListener(topics = "ai_service_topic", groupId = "ai_service_topic_group")
//    private void listen(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
//        realProcess(kafkaChapReceiveDto);
//    }
//
//    @KafkaListener(topics = "ai_service_topic.DLT", groupId = "ai_service_topic_group.DLT")
//    private void listen(ConsumerRecord<?, ?> record) {
//
//    }
//
//    @Transactional("aiServiceJpaTransactionManager")
//    public void process(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
//
//    }
//
//    @UseAiServiceRealDataSource
//    public void realProcess(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
//        process(kafkaChapReceiveDto);
//    }
//
//    @UseAiServiceDevDataSource
//    public void devProcess(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
//        process(kafkaChapReceiveDto);
//    }

}
