package com.brunosong.transfersystem.aiservice.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class AiCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_course_seq")
    private Long aiCourseSeq;

    @Column(name = "ai_course_name")
    private String aiCourseName;

    public void updateAiCourseName(String aiCourseName) {
        this.aiCourseName = aiCourseName;
    }

}
