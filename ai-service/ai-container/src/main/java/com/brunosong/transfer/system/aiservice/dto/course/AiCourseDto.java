package com.brunosong.transfer.system.aiservice.dto.course;

import com.brunosong.transfer.system.aiservice.domain.AiCourse;
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
