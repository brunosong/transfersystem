package com.brunosong.transfersystem.aiservice.infrastructure.course;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.domain.AiCourse;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ai_chap")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiCourseEntity {

    @Id
    @Column(name = "ai_course_seq")
    private Long aiCourseSeq;

    @Column(name = "ai_course_name")
    private String aiCourseName;


    public AiCourse toModel(){
        return AiCourse.builder()
            .aiCourseSeq(this.getAiCourseSeq())
            .aiCourseName(this.getAiCourseName())
            .build();
    }

    public static AiCourseEntity fromModel(AiCourse aiCourse) {
        return AiCourseEntity.builder()
                .aiCourseSeq(aiCourse.getAiCourseSeq())
                .aiCourseName(aiCourse.getAiCourseName())
                .build();
    }

}
