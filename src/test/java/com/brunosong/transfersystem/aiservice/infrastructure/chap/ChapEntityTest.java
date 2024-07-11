package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
import com.brunosong.transfersystem.aiservice.domain.Chap.ChapTypeEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ChapEntityTest {


    @Test
    void 엔티티클래스에서_도메인객체를_사용해서_엔티티객체로_변환할_수_있다(){

        //given
        Chap model = Chap.builder()
                .chapSeq(1L)
                .chapTitle("테스트 차시1")
                .chapType(ChapTypeEnum.KOR)
                .build();

        //when
        ChapEntity entity = ChapEntity.fromModel(model);

        //then
        Assertions.assertThat(model.getChapSeq()).isEqualTo(entity.getChapSeq());
        Assertions.assertThat(model.getChapTitle()).isEqualTo(entity.getChapTitle());
        Assertions.assertThat(model.getChapType().name()).isEqualTo(entity.getChapType());

    }


    @Test
    void 엔티티객체에서_도메인객체로_변환할_수_있다(){

        //given
        ChapEntity entity = ChapEntity.builder()
                .chapSeq(1L)
                .chapTitle("테스트 차시1")
                .chapType("KOR")
                .build();

        //when
        Chap model = entity.toModel();

        //then
        Assertions.assertThat(model.getChapSeq()).isEqualTo(entity.getChapSeq());
        Assertions.assertThat(model.getChapTitle()).isEqualTo(entity.getChapTitle());
        Assertions.assertThat(model.getChapType().name()).isEqualTo(entity.getChapType());
    }

}