package com.brunosong.transfer.system.transfer.dataaccess.course.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "main_course")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_seq")
    private Long courseSeq;

    @Column(name = "course_name")
    private String courseName;


}
