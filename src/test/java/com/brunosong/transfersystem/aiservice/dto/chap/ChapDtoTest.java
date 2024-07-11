package com.brunosong.transfersystem.aiservice.dto.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ChapDtoTest {


    @Test
    void SAVEDTO_에서_도메인객체로_변환할_수_있다(){

        ChapSaveDto chapSaveDto = new ChapSaveDto();
        chapSaveDto.setChapSeq(1L);
        chapSaveDto.setChapTitle("테스트 차시1");
        chapSaveDto.setChapType("ENG");

        Chap chap = chapSaveDto.toDomain();

        Assertions.assertThat(chapSaveDto.getChapSeq()).isEqualTo(chap.getChapSeq());
        Assertions.assertThat(chapSaveDto.getChapTitle()).isEqualTo(chap.getChapTitle());
        Assertions.assertThat(Chap.ChapTypeEnum.valueOf(chapSaveDto.getChapType())).isEqualTo(chap.getChapType());

    }

}