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

    // 학습 수준은 1~5 다. 배포의 원본 변환기가 반드시 요구하는 칸이라 비워 둘 수 없다
    public static final int MIN_LEARNING_LEVEL = 1;
    public static final int MAX_LEARNING_LEVEL = 5;
    private static final int DEFAULT_LEARNING_LEVEL = MIN_LEARNING_LEVEL;

    @EqualsAndHashCode.Include
    private final String id;
    private String title;
    private String description;
    private int learningLevel;

    private Lesson(String id, String title, String description, int learningLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.learningLevel = learningLevel;
    }

    /** 새 차시. ID는 시스템이 만든다. 학습 수준은 1로 시작하고, 고치기에서 바꾼다 */
    public static Lesson create(String title, String description) {
        checkTitle(title);
        return new Lesson("LESSON-" + UUID.randomUUID(), title, description, DEFAULT_LEARNING_LEVEL);
    }

    /** 저장소에서 읽은 값으로 다시 만든다 */
    public static Lesson restore(String id, String title, String description, int learningLevel) {
        return new Lesson(id, title, description, learningLevel);
    }

    // ─── 루트(SubjectDraft)만 부른다 ───

    void edit(String title, String description, int learningLevel) {
        checkTitle(title);
        checkLearningLevel(learningLevel);
        this.title = title;
        this.description = description;
        this.learningLevel = learningLevel;
    }

    private static void checkTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("차시 제목이 비어 있다");
        }
    }

    private static void checkLearningLevel(int learningLevel) {
        if (learningLevel < MIN_LEARNING_LEVEL || learningLevel > MAX_LEARNING_LEVEL) {
            throw new IllegalArgumentException(
                    "학습 수준은 " + MIN_LEARNING_LEVEL + "~" + MAX_LEARNING_LEVEL + " 이다: " + learningLevel);
        }
    }
}
