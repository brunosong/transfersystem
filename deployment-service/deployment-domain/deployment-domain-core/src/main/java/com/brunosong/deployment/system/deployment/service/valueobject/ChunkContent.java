package com.brunosong.deployment.system.deployment.service.valueobject;

import java.util.List;

/**
 * 대상에 보낼 청크 내용. 저작 문서의 모양과 메시지 계약(Avro) 사이에 둔 배포 쪽 모델이다.
 * 대상 쪽에 같은 모양이 또 있지만 공유하지 않는다. 두 서비스가 함께 지키는 것은 계약(.avsc)뿐이다.
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
