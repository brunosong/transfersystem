package com.brunosong.transfersystem.aiservice.kafka;

import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.aiservice.dto.kafka.KafkaReceiveDto.KafkaChapReceiveDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.AiChapService;
import com.brunosong.transfersystem.config.annotation.UseAiServiceDevDataSource;
import com.brunosong.transfersystem.config.annotation.UseAiServiceRealDataSource;
import com.brunosong.transfersystem.config.datasources.DataSourceType;
import com.brunosong.transfersystem.config.datasources.RoutingDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
//@ConditionalOnProperty(value = "spring.kafka.enabled" , havingValue = "true")
public class ChapConsumer {

    private final AiChapService chapService;
    private final ChapMapper chapMapper;

    @KafkaListener(topics = "ai_chap_topic", groupId = "ai_chap_topic_group")
    private void listen(KafkaChapReceiveDto kafkaChapReceiveDto) {
        realProcess(kafkaChapReceiveDto);
    }

    @KafkaListener(topics = "ai_chap_dev_topic", groupId = "ai_chap_dev_topic_group")
    private void listenDev(KafkaChapReceiveDto kafkaChapReceiveDto) {
        devProcess(kafkaChapReceiveDto);
    }

    public void process(KafkaChapReceiveDto kafkaChapReceiveDto) {
        if(null == kafkaChapReceiveDto.getChapTranDtoList()) {
            AiChapSaveDto aiChapSaveDto =
                    chapMapper.kafkaDtoToChapSaveDto(kafkaChapReceiveDto.getChapDto());

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
    public void realProcess(KafkaChapReceiveDto kafkaChapReceiveDto) {
        process(kafkaChapReceiveDto);
    }

    @UseAiServiceDevDataSource
    public void devProcess(KafkaChapReceiveDto kafkaChapReceiveDto) {
        process(kafkaChapReceiveDto);
    }

}
