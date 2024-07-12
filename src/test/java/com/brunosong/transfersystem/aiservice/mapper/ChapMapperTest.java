package com.brunosong.transfersystem.aiservice.mapper;

import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto;
import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ChapMapperTest.TestMapperConfig.class)
class ChapMapperTest {

    @Autowired
    ChapMapper chapMapper;

    @Test
    void ChapTranDto에서_ChapSaveDto로_변환된다() {
        ChapTranDto chapTranDto = new ChapTranDto();
        chapTranDto.setChapSeq(1L);
        chapTranDto.setChapTitle("테스트 차시1");
        chapTranDto.setChapType("KOR");

        ChapSaveDto chapSaveDto = chapMapper.toChapSaveDto(chapTranDto);

        assertThat(chapTranDto.getChapSeq()).isEqualTo(chapSaveDto.getChapSeq());
        assertThat(chapTranDto.getChapTitle()).isEqualTo(chapSaveDto.getChapTitle());
        assertThat(chapTranDto.getChapType()).isEqualTo(chapSaveDto.getChapType());
    }

    @TestConfiguration
    @ComponentScan(basePackages = "com.brunosong.transfersystem.aiservice.mapper")
    public static class TestMapperConfig {

    }
}