package com.brunosong.transfer.system.datamigration.service.domain.valueobject;

import java.util.List;

/**
 * 대상에 반영할 청크 내용. 메시지 계약(Avro)의 내용을 애플리케이션 계층이 쓰는 모양으로 옮긴 것이다.
 * 계약 클래스를 안쪽 계층까지 들이지 않으려고 따로 둔다.
 */
public sealed interface ChunkContent {

    /** 뼈대 청크: 커리큘럼, 학년, 학기와 학기별 과목 ID 목록 */
    record Skeleton(String title, String description, List<Grade> grades) implements ChunkContent {
    }

    /** 과목 청크: 과목 하나와 그 아래 단원, 차시 전부 */
    record SubjectChunk(String gradeId, String semesterId, Subject subject) implements ChunkContent {
    }

    record Grade(String id, String title, String description, List<Semester> semesters) {
    }

    record Semester(String id, String title, String description, List<String> subjectIds) {
    }

    record Subject(String id, String title, String description, List<Unit> units) {
    }

    record Unit(String id, String title, String description, List<Lesson> lessons) {
    }

    record Lesson(String id, String title, String description, int lessonNumber, int learningLevel) {
    }
}
