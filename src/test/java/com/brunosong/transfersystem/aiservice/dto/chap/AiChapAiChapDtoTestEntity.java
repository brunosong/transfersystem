package com.brunosong.transfersystem.aiservice.dto.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AiChapAiChapDtoTestEntity {


    @Test
    void SAVEDTO_에서_도메인객체로_변환할_수_있다(){

        AiChapDto.AiChapSaveDto aiChapSaveDto = new AiChapDto.AiChapSaveDto();
        aiChapSaveDto.setAiChapSeq(1L);
        aiChapSaveDto.setAiChapTitle("테스트 차시1");
        aiChapSaveDto.setAiChapType("ENG");

        AiChap aiChap = aiChapSaveDto.toDomain();

        Assertions.assertThat(aiChapSaveDto.getAiChapSeq()).isEqualTo(aiChap.getAiChapSeq());
        Assertions.assertThat(aiChapSaveDto.getAiChapTitle()).isEqualTo(aiChap.getAiChapTitle());
        Assertions.assertThat(AiChap.AiChapTypeEnum.valueOf(aiChapSaveDto.getAiChapType())).isEqualTo(aiChap.getAiChapType());

    }

}