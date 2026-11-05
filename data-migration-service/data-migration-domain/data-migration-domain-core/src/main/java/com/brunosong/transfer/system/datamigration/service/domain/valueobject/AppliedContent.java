package com.brunosong.transfer.system.datamigration.service.domain.valueobject;

import java.util.List;

/**
 * 대상이 실제로 저장한 내용. 명령의 값을 되돌려 주는 것이 아니라 저장한 뒤의 값을 담아야 검증이 뜻을 가진다.
 */
public sealed interface AppliedContent {

    /** removedSubjectIds 는 기록용이다. 지운 과목을 빼는 동작을 만들기 전까지는 비어 있다 */
    record Skeleton(int gradeCount, int semesterCount, List<String> removedSubjectIds) implements AppliedContent {
    }

    record Subject(String subjectId, String title, int unitCount, int lessonCount) implements AppliedContent {
    }
}
