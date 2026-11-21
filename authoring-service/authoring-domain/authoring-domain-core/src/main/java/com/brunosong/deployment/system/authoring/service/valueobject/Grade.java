package com.brunosong.deployment.system.authoring.service.valueobject;


import java.util.UUID;

/** 학년. 뼈대 초안 안에 들어 있다. 학기는 다음 기능에서 더한다 */
public record Grade(String id, String title, String description) {

    /** 새 학년. ID는 시스템이 만든다. (사람이 치면 겹치거나 고치고 싶어진다.) */
    public static Grade create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("학년 제목이 비어 있다.");
        }
        return new Grade("GRADE-" + UUID.randomUUID(), title, description);
    }

}
