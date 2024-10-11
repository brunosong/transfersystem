package com.brunosong.transfer.system.aiservice.kafka;

import com.brunosong.transfer.system.aiservice.dto.kafka.KafkaReceiveDto;
import com.brunosong.transfer.system.aiservice.mapper.ChapMapper;
import com.brunosong.transfer.system.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfer.system.aiservice.service.chap.AiChapService;
import com.brunosong.transfersystem.config.annotation.UseAiServiceDevDataSource;
import com.brunosong.transfersystem.config.annotation.UseAiServiceRealDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled" , havingValue = "true")   // 카푸카 리스너만 사용을 하지 않도록 설정
public class ChapConsumer {

    private final AiChapService chapService;
    private final ChapMapper chapMapper;

    @KafkaListener(topics = "ai_chap_topic", groupId = "ai_chap_topic_group", containerFactory = "manualCommitFactory")
    public void listenAiChapTopic(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto, Acknowledgment ack) {
        processMessage(kafkaChapReceiveDto,ack,true);
    }


    @KafkaListener(topics = "ai_chap_dev_topic", groupId = "ai_chap_dev_topic_group", containerFactory = "manualCommitFactory")
    public void listenAiChapDevTopic(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto, Acknowledgment ack) {
        processMessage(kafkaChapReceiveDto,ack,false);
    }

    private void processMessage(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto, Acknowledgment ack, boolean isReal) {
        try {
            if (isReal) {
                realProcess(kafkaChapReceiveDto);
            } else {
                devProcess(kafkaChapReceiveDto);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message: {}", kafkaChapReceiveDto.getChapTranDto().getChapSeq(), e);
        }
    }

    @KafkaListener(topics = "ai_chap_topic.DLT", groupId = "ai_chap_topic_group.DLT", containerFactory = "manualCommitFactory")
    public void listen(ConsumerRecord<?, ?> record) {
        processDLT(record);
    }

    @KafkaListener(topics = "ai_chap_dev_topic.DLT", groupId = "ai_chap_dev_topic_group.DLT", containerFactory = "manualCommitFactory")
    public void listenDev(ConsumerRecord<?, ?> record) {
        processDLT(record);
    }

    public void processDLT(ConsumerRecord<?, ?> record) {
        // Dead Letter 메시지 로그
        log.error("Dead Letter Topic - Key: {}, Value: {}, Partition: {}, Offset: {}",
                record.key(), record.value(), record.partition(), record.offset());

        // 메시지 재처리 로직 X

        // 메시지 데이터베이스에 저장
        // Todo. DLT 메시지 저장기능 구현
        // saveToDatabase(record);

        // 알림 전송
        // Todo. DLT 알림 기능 구현
        // sendAlert(record);
    }

    @Transactional("aiServiceJpaTransactionManager")
    public void process(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
        if(null == kafkaChapReceiveDto.getChapTranDtoList()) {
            AiChapSaveDto aiChapSaveDto =
                    chapMapper.kafkaDtoToChapSaveDto(kafkaChapReceiveDto.getChapTranDto());

            chapService.save(aiChapSaveDto);
        } else {
            List<AiChapSaveDto> aiChapSaveDtoList = kafkaChapReceiveDto.getChapTranDtoList().stream()
                    .map(chapMapper::kafkaDtoToChapSaveDto).collect(Collectors.toList());

            for (AiChapSaveDto aiChapSaveDto : aiChapSaveDtoList) {
                chapService.save(aiChapSaveDto);
            }
        }
    }

    @UseAiServiceRealDataSource
    public void realProcess(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
        process(kafkaChapReceiveDto);
    }

    @UseAiServiceDevDataSource
    public void devProcess(KafkaReceiveDto.KafkaChapReceiveDto kafkaChapReceiveDto) {
        process(kafkaChapReceiveDto);
    }

}
