package com.brunosong.deployment.system.authoring.service.entity;

import com.brunosong.deployment.system.authoring.service.exception.PublishRejectedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 판. 공개한 순간의 커리큘럼 전체를 찍어 둔 것이다. 한 번 만들면 고치지 않는다(불변식 1).
 * 초안(뼈대 1개 + 과목 N개)을 모아 배포가 읽는 원본 문서 모양 하나로 조립한다.
 * 안의 학년·차시는 바뀌지 않는 값이라 record 로 둔다(초안의 엔티티와 다르다)
 */
public record CurriculumVersion(
        String curriculumId,
        int version,                  // 판 번호. 1부터 1씩 커진다(불변식 2)
        String title,
        String description,
        int sortOrder,
        List<Grade> grades,
        BasedOn basedOn,              // 어떤 초안의 어느 revision 으로 만들었나
        Instant publishedAt) {

    public record Grade(String id, String title, String description, List<Semester> semesters) {
    }

    public record Semester(String id, String title, String description, List<Subject> subjects) {
    }

    public record Subject(String id, String title, String description, List<Unit> units) {
    }

    public record Unit(String id, String title, String description, List<Lesson> lessons) {
    }

    /**
     * lessonNumber 는 단원 안의 순서대로 1부터 매긴다. 배포의 변환기가 반드시 요구한다
     */
    public record Lesson(String id, String title, String description, int lessonNumber, int learningLevel) {
    }

    /**
     * 판을 만든 초안의 revision. 다음 공개 때 "바뀐 것이 없으면 공개하지 않는다"(불변식 5)를 이것으로 본다
     */
    public record BasedOn(int skeletonRevision, Map<String, Integer> subjectRevisions) {
    }

    /**
     * 초안을 모아 판을 만든다.
     * 과목은 자기가 가리키는 학기 밑으로 들어가고, 한 학기 안에서는 sortOrder 순이다.
     * 과목이 가리키는 학기가 뼈대에 없으면 거절한다. 학기 삭제와 과목 만들기가 겹쳤을 때 생길 수 있는 어긋남을
     * 판에 넣지 않으려는 마지막 확인이다(ADR 0009)
     */
    public static CurriculumVersion assemble(int version, SkeletonDraft skeleton, List<SubjectDraft> subjects,
                                             Instant publishedAt) {
        // 1. 과목이 가리키는 학기가 뼈대에 다 있는지 본다
        for (SubjectDraft subject : subjects) {
            if (!skeleton.hasSemester(subject.getSemesterId())) {
                throw new PublishRejectedException(
                        "과목 '" + subject.getTitle() + "' 이 가리키는 학기가 뼈대에 없다. 과목을 다른 학기로 옮기거나 지운 뒤 공개하라");
            }
        }

        // 2. 뼈대의 학년 → 학기 순서대로 내려가며, 학기마다 그 학기의 과목을 끼워 넣는다
        List<Grade> grades = new ArrayList<>();
        for (com.brunosong.deployment.system.authoring.service.entity.Grade g : skeleton.getGrades()) {
            List<Semester> semesters = new ArrayList<>();
            for (com.brunosong.deployment.system.authoring.service.entity.Semester s : g.getSemesters()) {
                List<Subject> subjectsOfSemester = subjects.stream()
                        .filter(sub -> sub.getSemesterId().equals(s.getId()))
                        .sorted(Comparator.comparingInt(SubjectDraft::getSortOrder).thenComparing(SubjectDraft::getTitle))
                        .map(CurriculumVersion::toSubject)
                        .toList();
                semesters.add(new Semester(s.getId(), s.getTitle(), s.getDescription(), subjectsOfSemester));
            }
            grades.add(new Grade(g.getId(), g.getTitle(), g.getDescription(), semesters));
        }

        // 3. 어떤 revision 들로 만들었는지 남긴다
        BasedOn basedOn = new BasedOn(skeleton.getRevision(),
                subjects.stream().collect(Collectors.toMap(SubjectDraft::getId, SubjectDraft::getRevision)));

        return new CurriculumVersion(skeleton.getCurriculumId(), version, skeleton.getTitle(),
                skeleton.getDescription(), skeleton.getSortOrder(), grades, basedOn, publishedAt);
    }

    private static Subject toSubject(SubjectDraft subject) {
        List<Unit> units = subject.getUnits().stream()
                .map(u -> {
                    List<Lesson> lessons = new ArrayList<>();
                    int number = 1;
                    for (com.brunosong.deployment.system.authoring.service.entity.Lesson l : u.getLessons()) {
                        lessons.add(new Lesson(l.getId(), l.getTitle(), l.getDescription(), number++, l.getLearningLevel()));
                    }
                    return new Unit(u.getId(), u.getTitle(), u.getDescription(), lessons);
                })
                .toList();
        return new Subject(subject.getId(), subject.getTitle(), subject.getDescription(), units);
    }

}
