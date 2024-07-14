package com.brunosong.transfersystem.aiservice.dto.course;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto;
import lombok.Getter;
import lombok.Setter;

public class AiCourseDto {

    @Getter
    @Setter
    public static class AiCourseSaveDto {

        Long aiCourseSeq;
        String aiCourseName;

        public AiCourse toDomain() {
            return AiCourse.builder()
                    .aiCourseSeq(this.getAiCourseSeq())
                    .aiCourseName(this.getAiCourseName())
                    .build();
        }

    }


    @Getter
    @Setter
    public static class AiCourseRespDto {

        Long aiCourseSeq;
        String aiCourseName;

        public static AiCourseRespDto fromDomain(AiCourse aiCourse) {
            AiCourseRespDto aiChapRespDto = new AiCourseRespDto();
            aiChapRespDto.setAiCourseSeq(aiCourse.getAiCourseSeq());
            aiChapRespDto.setAiCourseName(aiCourse.getAiCourseName());
            return aiChapRespDto;
        }

    }

}
