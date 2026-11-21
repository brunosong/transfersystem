package com.brunosong.deployment.system.authoring.service.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 학년. 뼈대 초안 안에 있는 엔티티다(ID로 구별한다). 학기를 담는다.
 * 고치는 메서드는 public 이 아니다. 같은 패키지의 SkeletonDraft(애그리거트 루트)만 부를 수 있다.
 * 루트를 거쳐야 revision 이 오른다
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // 같은지는 ID로만 본다
public class Grade {

    @EqualsAndHashCode.Include
    private final String id;
    private String title;
    private String description;
    private final List<Semester> semesters;

    private Grade(String id, String title, String description, List<Semester> semesters) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.semesters = new ArrayList<>(semesters);   // 안에서 고칠 수 있게 우리 목록으로 복사한다
    }

    /** 새 학년. 학기는 아직 없다. ID는 시스템이 만든다 */
    public static Grade create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("학년 제목이 비어 있다");
        }
        return new Grade("GRADE-" + UUID.randomUUID(), title, description, List.of());
    }

    /** 저장소에서 읽은 값으로 다시 만든다 */
    public static Grade restore(String id, String title, String description, List<Semester> semesters) {
        return new Grade(id, title, description, semesters);
    }

    /** 밖에서는 읽기만 한다. 고치려면 SkeletonDraft 를 거친다 */
    public List<Semester> getSemesters() {
        return Collections.unmodifiableList(semesters);
    }

    // ─── 루트(SkeletonDraft)만 부른다 ───

    void addSemester(Semester semester) {
        semesters.add(semester);
    }
}
