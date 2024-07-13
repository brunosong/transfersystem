package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AiChapChapEntityTestEntity {


    @Test
    void 엔티티클래스에서_도메인객체를_사용해서_엔티티객체로_변환할_수_있다(){

        //given
        AiChap model = AiChap.builder()
                .aiChapSeq(1L)
                .aiChapTitle("테스트 차시1")
                .aiChapType(AiChap.AiChapTypeEnum.KOR)
                .build();

        //when
        AiChapEntity entity = AiChapEntity.fromModel(model);

        //then
        Assertions.assertThat(model.getAiChapSeq()).isEqualTo(entity.getAiChapSeq());
        Assertions.assertThat(model.getAiChapTitle()).isEqualTo(entity.getAiChapTitle());
        Assertions.assertThat(model.getAiChapType().name()).isEqualTo(entity.getAiChapType());

    }


    @Test
    void 엔티티객체에서_도메인객체로_변환할_수_있다(){

        //given
        AiChapEntity entity = AiChapEntity.builder()
                .aiChapSeq(1L)
                .aiChapTitle("테스트 차시1")
                .aiChapType("KOR")
                .build();

        //when
        AiChap model = entity.toModel();

        //then
        Assertions.assertThat(model.getAiChapSeq()).isEqualTo(entity.getAiChapSeq());
        Assertions.assertThat(model.getAiChapTitle()).isEqualTo(entity.getAiChapTitle());
        Assertions.assertThat(model.getAiChapType().name()).isEqualTo(entity.getAiChapType());
    }

}