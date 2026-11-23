package com.brunosong.deployment.system.authoring.service.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 단원. 과목 초안 안에 있는 엔티티다(ID로 구별한다.) 차시는 다음 기능에서 더한다.
 * 고치는 메서드는 public이 아니다. 같은 패키지의 SubjectDraft(애그리거트 루트)만 부를 수 있다.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //같은지는 ID로만 본다.
public class Unit {


    @EqualsAndHashCode.Include
    private final String id;
    private String title;
    private String description;

    private Unit(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /** 새 단원. ID는 시스템이 만든다. */
    public static Unit create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("단원 제목이 비어 있다");
        }
        return new Unit("UNIT-" + UUID.randomUUID() ,title, description);
    }

    /** 저장소에서 읽은 값으로 다시 만든다 */
    public static Unit restore(String id, String title, String description) {
        return new Unit(id, title, description);
    }
}
