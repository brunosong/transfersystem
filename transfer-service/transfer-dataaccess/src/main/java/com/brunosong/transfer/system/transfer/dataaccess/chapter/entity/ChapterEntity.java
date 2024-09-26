package com.brunosong.transfer.system.transfer.dataaccess.chapter.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "main_chap")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chap_seq")
    private Long chapSeq;

    @Column(name = "chap_title")
    private String chapTitle;

    @Column(name = "chap_type")
    @Enumerated(value = EnumType.STRING)
    private ChapterTypeEnum chapType;

    public enum ChapterTypeEnum {
        KOR,
        ENG,
        MATH
    }



}
