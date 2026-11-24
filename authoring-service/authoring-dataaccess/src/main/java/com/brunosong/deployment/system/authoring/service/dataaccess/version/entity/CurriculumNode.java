package com.brunosong.deployment.system.authoring.service.dataaccess.version.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 판의 커리큘럼 트리. 배포가 읽는 원본 문서 모양(시스템 설계 "원본 읽기" 4.1)을 그대로 따른다.
 * - 목록 칸 이름이 단수다(grade, semester, subject, unit, lesson)
 * - 안쪽 ID 칸 이름이 id 다. Spring 은 id 라는 이름을 _id 로 바꿔 저장하므로 @Field("id") 로 이름을 못 박는다
 */
public record CurriculumNode(String curriculumId, String title, String description, int sortOrder,
                             List<GradeNode> grade) {

    public record GradeNode(@Field("id") String id, String title, String description, List<SemesterNode> semester) {
    }

    public record SemesterNode(@Field("id") String id, String title, String description, List<SubjectNode> subject) {
    }

    public record SubjectNode(@Field("id") String id, String title, String description, List<UnitNode> unit) {
    }

    public record UnitNode(@Field("id") String id, String title, String description, List<LessonNode> lesson) {
    }

    public record LessonNode(@Field("id") String id, String title, String description, int lessonNumber,
                             int learningLevel) {
    }
}
