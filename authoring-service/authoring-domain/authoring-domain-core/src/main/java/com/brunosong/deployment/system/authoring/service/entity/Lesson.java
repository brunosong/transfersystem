package com.brunosong.deployment.system.authoring.service.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 차시. 단원 안에 있는 엔티티다(ID로 구별한다). 커리큘럼 트리의 맨 아래 단계다.
 * 고치는 메서드는 public 이 아니다. SubjectDraft(애그리거트 루트)를 거쳐서만 고친다
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // 같은지는 ID로만 본다
public class Lesson {

    @EqualsAndHashCode.Include
    private final String id;
    private String title;
    private String description;

    private Lesson(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /** 새 차시. ID는 시스템이 만든다 */
    public static Lesson create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("차시 제목이 비어 있다");
        }
        return new Lesson("LESSON-" + UUID.randomUUID(), title, description);
    }

    /** 저장소에서 읽은 값으로 다시 만든다 */
    public static Lesson restore(String id, String title, String description) {
        return new Lesson(id, title, description);
    }
}
