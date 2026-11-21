package com.brunosong.deployment.system.authoring.service.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/** 학기. 학년 안에 있는 엔티티다(ID로 구별한다). 과목은 학기를 ID로 가리킨다(과목은 따로 저장한다) */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // 같은지는 ID로만 본다
public class Semester {

    @EqualsAndHashCode.Include
    private final String id;
    private String title;
    private String description;

    private Semester(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /** 새 학기. ID는 시스템이 만든다 */
    public static Semester create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("학기 제목이 비어 있다");
        }
        return new Semester("SEMESTER-" + UUID.randomUUID(), title, description);
    }

    /** 저장소에서 읽은 값으로 다시 만든다 */
    public static Semester restore(String id, String title, String description) {
        return new Semester(id, title, description);
    }

    void edit(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("학기 제목이 비어 있다");
        }
        this.title = title;
        this.description = description;
    }
}
