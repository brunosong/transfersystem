package com.brunosong.deployment.system.authoring.service.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** 학년. 뼈대 초안 안에 들어 있고, 학기를 담는다 */
public record Grade(String id, String title, String description, List<Semester> semesters) {

    // record 의 생성자에 검사를 붙이는 방법(compact constructor). 목록을 복사해 밖에서 못 고치게 한다
    public Grade {
        semesters = List.copyOf(semesters);
    }

    /** 새 학년. 학기는 아직 없다 */
    public static Grade create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("학년 제목이 비어 있다");
        }
        return new Grade("GRADE-" + UUID.randomUUID(), title, description, List.of());
    }

    /** 학기를 맨 뒤에 붙인 새 학년 */
    public Grade addSemester(Semester semester) {
        List<Semester> added = new ArrayList<>(semesters);
        added.add(semester);
        return new Grade(id, title, description, added);
    }
}
