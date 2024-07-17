package com.brunosong.transfersystem.aiservice.kafka;

import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.aiservice.dto.kafka.KafkaReceiveDto.KafkaChapDto;
import com.brunosong.transfersystem.aiservice.dto.kafka.KafkaReceiveDto.KafkaChapReceiveDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.AiChapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ChapConsumerTest {

    @Mock
    AiChapService chapService;

    @Mock
    ChapMapper chapMapper;

    @Mock
    Acknowledgment acknowledgment;

    @InjectMocks
    ChapConsumer chapConsumer;

    @Test
    void listenAiChapTopic_리스너가_단건을_저장하고_ACK_를_커밋한다() {
        KafkaChapDto chapTranDto = new KafkaChapDto();
        chapTranDto.setChapSeq(1L);

        KafkaChapReceiveDto kafkaChapReceiveDto = new KafkaChapReceiveDto();
        kafkaChapReceiveDto.setChapTranDto(chapTranDto);

        AiChapSaveDto aiChapSaveDt = new AiChapSaveDto();
        when(chapMapper.kafkaDtoToChapSaveDto(any())).thenReturn(aiChapSaveDt);

        chapConsumer.listenAiChapTopic(kafkaChapReceiveDto,acknowledgment);

        verify(chapService, times(1)).save(any(AiChapSaveDto.class));
        verify(acknowledgment, times(1)).acknowledge();
    }



}