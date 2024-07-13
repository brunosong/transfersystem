package com.brunosong.transfersystem.aiservice.infrastructure.course;

import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AiCourseEntityTest {

    @Test
    void 엔티티클래스에서_도메인객체를_사용해서_엔티티객체로_변환할_수_있다(){

        //given
        AiCourse model = AiCourse.builder()
                .aiCourseSeq(1L)
                .aiCourseName("테스트 코스")
                .build();

        //when
        AiCourseEntity entity = AiCourseEntity.fromModel(model);

        //then
        Assertions.assertThat(model.getAiCourseSeq()).isEqualTo(entity.getAiCourseSeq());
        Assertions.assertThat(model.getAiCourseName()).isEqualTo(entity.getAiCourseName());

    }


    @Test
    void 엔티티객체에서_도메인객체로_변환할_수_있다(){

        //given
        AiCourseEntity entity = AiCourseEntity.builder()
                .aiCourseSeq(1L)
                .aiCourseName("테스트 코스")
                .build();

        //when
        AiCourse model = entity.toModel();

        //then
        Assertions.assertThat(model.getAiCourseSeq()).isEqualTo(entity.getAiCourseSeq());
        Assertions.assertThat(model.getAiCourseName()).isEqualTo(entity.getAiCourseName());
    }

}