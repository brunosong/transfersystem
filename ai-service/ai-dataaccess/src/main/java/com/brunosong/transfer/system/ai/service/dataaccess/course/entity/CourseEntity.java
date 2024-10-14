package com.brunosong.transfer.system.ai.service.dataaccess.course.entity;

import com.brunosong.transfer.system.ai.service.dataaccess.chapter.entity.ChapterEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @OrderBy("chapOrder ASC")
    private List<ChapterEntity> chapterList = new ArrayList<>();

    public void addChapter(ChapterEntity chapterEntity) {
        chapterEntity.setCourse(this);
        chapterList.add(chapterEntity);
        chapterEntity.setChapOrder(chapterList.size());
    }
}
