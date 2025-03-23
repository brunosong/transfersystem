package com.brunosong.transfer.system.datamigration.service.dataaccess.bootcamp.entity;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.CourseEntity;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Entity
@Table(name = "ai_chapter")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chap_seq")
    private Long chapSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseSeq")
    private CourseEntity course;

    @Column(name = "chap_title")
    private String chapTitle;

    @Column(name = "chap_type")
    @Enumerated(value = EnumType.STRING)
    private ChapterTypeEnum chapType;

    @Column(name = "chap_order")
    private int chapOrder;

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public void setChapOrder(int chapOrder) {
        this.chapOrder = chapOrder;
    }

    public enum ChapterTypeEnum {
        KOR,
        ENG,
        MATH
    }

}
