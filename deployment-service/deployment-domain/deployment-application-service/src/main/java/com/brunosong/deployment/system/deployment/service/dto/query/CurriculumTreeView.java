package com.brunosong.deployment.system.deployment.service.dto.query;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;

import java.util.List;

/**
 * 화면에 보여 줄 커리큘럼 트리. 문서가 커서 과목까지만 내려 주고 단원·차시는 개수만 준다.
 * chunkCount 는 배포하면 나뉘는 청크 수다. 나누는 규칙이 한 곳에 있도록 ChunkContent.split 으로 센다.
 */
public record CurriculumTreeView(String curriculumId, String title, String description,
                                 Counts counts, int chunkCount, List<GradeNode> grades) {

    public record Counts(int grades, int semesters, int subjects, int units, int lessons) {
    }

    public record GradeNode(String id, String title, List<SemesterNode> semesters) {
    }

    public record SemesterNode(String id, String title, List<SubjectNode> subjects) {
    }

    public record SubjectNode(String id, String title, int unitCount, int lessonCount) {
    }

    public static CurriculumTreeView from(Curriculum curriculum) {
        List<Curriculum.Semester> semesters = curriculum.grades().stream()
                .flatMap(grade -> grade.semesters().stream()).toList();
        List<Curriculum.Subject> subjects = semesters.stream()
                .flatMap(semester -> semester.subjects().stream()).toList();
        int units = subjects.stream().mapToInt(subject -> subject.units().size()).sum();
        int lessons = subjects.stream().mapToInt(CurriculumTreeView::lessonCount).sum();

        List<GradeNode> grades = curriculum.grades().stream()
                .map(grade -> new GradeNode(grade.id(), grade.title(), grade.semesters().stream()
                        .map(semester -> new SemesterNode(semester.id(), semester.title(), semester.subjects().stream()
                                .map(subject -> new SubjectNode(subject.id(), subject.title(),
                                        subject.units().size(), lessonCount(subject)))
                                .toList()))
                        .toList()))
                .toList();

        return new CurriculumTreeView(curriculum.curriculumId(), curriculum.title(), curriculum.description(),
                new Counts(curriculum.grades().size(), semesters.size(), subjects.size(), units, lessons),
                ChunkContent.split(curriculum).size(), grades);
    }

    private static int lessonCount(Curriculum.Subject subject) {
        return subject.units().stream().mapToInt(unit -> unit.lessons().size()).sum();
    }
}
