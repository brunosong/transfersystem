package com.brunosong.transfersystem.aiservice.kafka;

import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.aiservice.dto.kafka.KafkaReceiveDto.KafkaChapReceiveDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.AiChapService;
import com.brunosong.transfersystem.config.kafka.AiServiceConsumerConfig;
import com.brunosong.transfersystem.config.kafka.KafkaConfig;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.brunosong.transfersystem.aiservice.dto.kafka.KafkaReceiveDto.KafkaChapDto;
import static com.brunosong.transfersystem.main.service.MigrationAiKafkaService.KafkaSendDto;
import static com.brunosong.transfersystem.main.service.MigrationAiKafkaService.KafkaSendListDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(classes = { KafkaAutoConfiguration.class ,
                            ChapConsumer.class,
                            AiServiceConsumerConfig.class,
                            KafkaConfig.class })
@ActiveProfiles("kafka-test")
@EmbeddedKafka(topics = {"test",
                         "test.DLT"}, ports = {9092} ,brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChapConsumerIntegrationTest {

    @Autowired
    KafkaTemplate<String,Object> kafkaTemplate;

    @MockBean
    AiChapService chapService;

    @MockBean
    ChapMapper chapMapper;

    @SpyBean
    ChapConsumer chapConsumer;

    @Captor
    ArgumentCaptor<KafkaChapReceiveDto> dtoArgumentCaptor;

    @Captor
    ArgumentCaptor<Acknowledgment> ackArgumentCaptor;


    @Test
    public void 카푸카리스너가_ChapTranDto_하나의_객체를_제대로_맵핑하는지_테스트한다() {

        // given
        AiChapSaveDto aiChapSaveDto = new AiChapSaveDto();
        aiChapSaveDto.setAiChapSeq(1L);

        ChapTranDto chapTranDto = new ChapTranDto();
        chapTranDto.setChapSeq(1L);

        KafkaSendDto kafkaSendDto = new KafkaSendDto();
        kafkaSendDto.setChapTranDto(chapTranDto);

        // when
        kafkaTemplate.send("ai_chap_topic", kafkaSendDto );

        // stub
        given(chapMapper.kafkaDtoToChapSaveDto(any())).willReturn(aiChapSaveDto);

        // captor
        verify(chapConsumer, timeout(5000).times(1)).listenAiChapTopic(
                dtoArgumentCaptor.capture(), ackArgumentCaptor.capture()
        );

        // chapMapper.kafkaDtoToChapSaveDto() 동작을 한번 실행했는지 체크한다.
        verify(chapMapper,times(1)).kafkaDtoToChapSaveDto(any());

        // chapService.save()가 한번 실행했는지 체크한다.
        verify(chapService, times(1)).save(any());

        // kafkaTemplate.send 로 보낸 메시지가 listenAiChapTopic() 에 원하는데로 맵핑이 되었는지 체크한다.
        Assertions.assertThat(chapTranDto.getChapSeq()).isEqualTo(dtoArgumentCaptor.getValue().getChapTranDto().getChapSeq());

    }


    @Test
    public void 카푸카리스너가_ChapTranDtoList_를_제대로_맵핑하는지_테스트한다() {

        // given
        AiChapSaveDto aiChapSaveDto = new AiChapSaveDto();
        aiChapSaveDto.setAiChapSeq(1L);

        ChapTranDto chapTranDto1 = new ChapTranDto();
        chapTranDto1.setChapSeq(1L);

        ChapTranDto chapTranDto2 = new ChapTranDto();
        chapTranDto2.setChapSeq(2L);

        List<ChapTranDto> tranDtoList = new ArrayList<>();
        tranDtoList.add(chapTranDto1);
        tranDtoList.add(chapTranDto2);

        KafkaSendListDto kafkaSendListDto = new KafkaSendListDto();
        kafkaSendListDto.setChapTranDtoList(tranDtoList);

        // when
        kafkaTemplate.send("ai_chap_topic", kafkaSendListDto );

        // stub
        given(chapMapper.kafkaDtoToChapSaveDto(any())).willReturn(aiChapSaveDto);

        // captor
        verify(chapConsumer, timeout(5000).times(1)).listenAiChapTopic(
                dtoArgumentCaptor.capture(), ackArgumentCaptor.capture()
        );


        // then
        // 두건을 보냈기에 두번씩 실행되어야 한다.
        verify(chapMapper,times(2)).kafkaDtoToChapSaveDto(any());
        verify(chapService, times(2)).save(any());

        List<KafkaChapDto> chapTranDtoList = dtoArgumentCaptor.getValue().getChapTranDtoList();

        Assertions.assertThat(chapTranDtoList).hasSize(2);
        Assertions.assertThat(chapTranDtoList)
                .extracting("chapSeq")
                .containsExactly(
                        1L,2L
                );


    }

}