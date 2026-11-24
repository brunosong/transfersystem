package com.brunosong.deployment.system.authoring.service.dataaccess.version.mapper;

import com.brunosong.deployment.system.authoring.service.dataaccess.version.entity.CurriculumNode;
import com.brunosong.deployment.system.authoring.service.dataaccess.version.entity.CurriculumVersionEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.version.entity.PublishedCurriculumEntity;
import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.VersionSummary;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 판 ↔ 창고(curriculum_versions)·진열대(published_curricula) 문서.
 * curriculum 칸은 배포가 읽는 원본 모양(CurriculumNode)이다. 두 곳이 같은 모양을 쓴다
 */
@Component
public class CurriculumVersionDataAccessMapper {

    /** 창고에 쌓을 문서. _id 는 커리큘럼:판 번호 */
    public CurriculumVersionEntity toVersionEntity(CurriculumVersion version) {
        return new CurriculumVersionEntity(
                CurriculumVersionEntity.idOf(version.curriculumId(), version.version()),
                version.curriculumId(), version.version(), version.publishedAt(),
                new CurriculumVersionEntity.BasedOnNode(version.basedOn().skeletonRevision(),
                        version.basedOn().subjectRevisions()),
                toNode(version));
    }

    /** 진열대에 놓을 문서. _id 는 커리큘럼 ID 라 커리큘럼마다 하나다 */
    public PublishedCurriculumEntity toPublishedEntity(CurriculumVersion version) {
        return new PublishedCurriculumEntity(version.curriculumId(), version.version(), version.publishedAt(),
                toNode(version));
    }

    /** 창고 문서를 판으로 되돌린다 */
    public CurriculumVersion toDomain(CurriculumVersionEntity e) {
        CurriculumNode c = e.curriculum();
        return new CurriculumVersion(e.curriculumId(), e.version(), c.title(), c.description(), c.sortOrder(),
                c.grade().stream().map(g -> new CurriculumVersion.Grade(g.id(), g.title(), g.description(),
                        g.semester().stream().map(s -> new CurriculumVersion.Semester(s.id(), s.title(), s.description(),
                                s.subject().stream().map(this::toSubject).toList()))
                                .toList()))
                        .toList(),
                toBasedOn(e.basedOn()), e.publishedAt());
    }

    /** 판 기록 한 줄. 트리(curriculum)를 빼고 읽은 문서에서 만든다 */
    public VersionSummary toSummary(CurriculumVersionEntity e) {
        return new VersionSummary(e.version(), e.publishedAt(), toBasedOn(e.basedOn()));
    }

    // ─── 판 → 원본 모양 ───

    private CurriculumNode toNode(CurriculumVersion v) {
        return new CurriculumNode(v.curriculumId(), v.title(), v.description(), v.sortOrder(),
                v.grades().stream().map(g -> new CurriculumNode.GradeNode(g.id(), g.title(), g.description(),
                        g.semesters().stream().map(s -> new CurriculumNode.SemesterNode(s.id(), s.title(), s.description(),
                                s.subjects().stream().map(this::toSubjectNode).toList()))
                                .toList()))
                        .toList());
    }

    private CurriculumNode.SubjectNode toSubjectNode(CurriculumVersion.Subject s) {
        return new CurriculumNode.SubjectNode(s.id(), s.title(), s.description(),
                s.units().stream().map(u -> new CurriculumNode.UnitNode(u.id(), u.title(), u.description(),
                        u.lessons().stream().map(l -> new CurriculumNode.LessonNode(l.id(), l.title(), l.description(),
                                l.lessonNumber(), l.learningLevel())).toList()))
                        .toList());
    }

    // ─── 원본 모양 → 판 ───

    private CurriculumVersion.Subject toSubject(CurriculumNode.SubjectNode s) {
        return new CurriculumVersion.Subject(s.id(), s.title(), s.description(),
                s.unit().stream().map(u -> new CurriculumVersion.Unit(u.id(), u.title(), u.description(),
                        u.lesson().stream().map(l -> new CurriculumVersion.Lesson(l.id(), l.title(), l.description(),
                                l.lessonNumber(), l.learningLevel())).toList()))
                        .toList());
    }

    private CurriculumVersion.BasedOn toBasedOn(CurriculumVersionEntity.BasedOnNode b) {
        return new CurriculumVersion.BasedOn(b.skeletonRevision(), Map.copyOf(b.subjectRevisions()));
    }
}
