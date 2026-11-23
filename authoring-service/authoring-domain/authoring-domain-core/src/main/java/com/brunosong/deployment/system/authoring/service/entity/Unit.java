package com.brunosong.deployment.system.authoring.service.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 단원. 과목 초안 안에 있는 엔티티다(ID로 구별한다). 차시를 담는다.
 * 고치는 메서드는 public 이 아니다. 같은 패키지의 SubjectDraft(애그리거트 루트)만 부를 수 있다.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // 같은지는 ID로만 본다
public class Unit {

    @EqualsAndHashCode.Include
    private final String id;
    private String title;
    private String description;
    private final List<Lesson> lessons;

    private Unit(String id, String title, String description, List<Lesson> lessons) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lessons = new ArrayList<>(lessons);   // 안에서 고칠 수 있게 우리 목록으로 복사한다
    }

    /** 새 단원. 차시는 아직 없다. ID는 시스템이 만든다 */
    public static Unit create(String title, String description) {
        checkTitle(title);
        return new Unit("UNIT-" + UUID.randomUUID(), title, description, List.of());
    }

    /** 저장소에서 읽은 값으로 다시 만든다 */
    public static Unit restore(String id, String title, String description, List<Lesson> lessons) {
        return new Unit(id, title, description, lessons);
    }

    /** 밖에서는 읽기만 한다. 고치려면 SubjectDraft 를 거친다 */
    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    // ─── 루트(SubjectDraft)만 부른다 ───

    void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    void edit(String title, String description) {
        checkTitle(title);
        this.title = title;
        this.description = description;
    }

    private static void checkTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("단원 제목이 비어 있다");
        }
    }

    void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }
}
