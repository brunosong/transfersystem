package com.brunosong.deployment.system.authoring.service.entity;

import java.util.UUID;

/** 학기. 학년 안에 들어 있다. 과목은 학기를 ID로 가리킨다(과목은 따로 저장한다) */
public record Semester(String id, String title, String description) {

    public static Semester create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("학기 제목이 비어 있다");
        }
        return new Semester("SEMESTER-" + UUID.randomUUID(), title, description);
    }
}