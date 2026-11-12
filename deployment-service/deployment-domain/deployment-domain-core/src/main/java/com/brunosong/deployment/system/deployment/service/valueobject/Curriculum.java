package com.brunosong.deployment.system.deployment.service.valueobject;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;

import java.util.List;

/**
 * 배포할 커리큘럼 하나. 원본 문서의 학년부터 차시까지를 그대로 옮긴 트리다.
 * 원본이 만들어 배포에 넘기고, 배포는 이것을 청크로 나눈다. 청크 번호나 뼈대 같은 배포의 말은 없다.
 * 만든 뒤 바뀌지 않는다.
 */
public record Curriculum(String curriculumId, String title, String description, List<Grade> grades) {

    public Curriculum {
        requireText(curriculumId, "curriculumId");
        requireText(title, "title");
        grades = List.copyOf(grades);
    }

    public record Grade(String id, String title, String description, List<Semester> semesters) {
        public Grade {
            semesters = List.copyOf(semesters);
        }
    }

    public record Semester(String id, String title, String description, List<Subject> subjects) {
        public Semester {
            subjects = List.copyOf(subjects);
        }
    }

    public record Subject(String id, String title, String description, List<Unit> units) {
        public Subject {
            units = List.copyOf(units);
        }
    }

    /** 단원. attributes는 아는 칸을 뺀 나머지 원본 칸을 담은 JSON 객체 문자열이다. 없으면 "{}"로 두고 null은 쓰지 않는다 */
    public record Unit(String id, String title, String description, String attributes, List<Lesson> lessons) {
        public Unit {
            if (attributes == null) attributes = "{}";
            lessons = List.copyOf(lessons);
        }
    }

    /** 차시. learningLevel은 받는 쪽이 attributes로 옮겨 갈 때까지만 함께 싣는다 */
    public record Lesson(String id, String title, String description, int lessonNumber, int learningLevel, String attributes) {
        public Lesson {
            if (attributes == null) attributes = "{}";
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DeploymentDomainException("커리큘럼의 " + field + " 가 비어 있다");
        }
    }

}
