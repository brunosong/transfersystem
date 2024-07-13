package com.brunosong.transfersystem.aiservice.mapper;

import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AiChapChapMapperTestEntity.TestMapperConfig.class)
class AiChapChapMapperTestEntity {

    @Autowired
    ChapMapper chapMapper;

    @Test
    void ChapTranDto에서_AiChapSaveDto로_변환된다() {
        ChapTranDto chapTranDto = new ChapTranDto();
        chapTranDto.setChapSeq(1L);
        chapTranDto.setChapTitle("테스트 차시1");
        chapTranDto.setChapType("KOR");

        AiChapSaveDto aiChapSaveDto = chapMapper.toChapSaveDto(chapTranDto);

        assertThat(chapTranDto.getChapSeq()).isEqualTo(aiChapSaveDto.getAiChapSeq());
        assertThat(chapTranDto.getChapTitle()).isEqualTo(aiChapSaveDto.getAiChapTitle());
        assertThat(chapTranDto.getChapType()).isEqualTo(aiChapSaveDto.getAiChapType());
    }

    @TestConfiguration
    @ComponentScan(basePackages = "com.brunosong.transfersystem.aiservice.mapper")
    public static class TestMapperConfig {

    }
}