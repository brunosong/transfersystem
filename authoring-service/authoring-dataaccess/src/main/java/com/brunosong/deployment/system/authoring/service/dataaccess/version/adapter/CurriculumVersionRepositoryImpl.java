package com.brunosong.deployment.system.authoring.service.dataaccess.version.adapter;

import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.CurriculumVersionRepository;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 판을 두 곳에 쓴다.
 * - curriculum_versions: 모든 판을 쌓는다(창고). _id 는 "CUR003:2" 처럼 커리큘럼:판 번호
 * - published_curricula: 커리큘럼마다 최신 판 하나(진열대). 배포가 읽는다. _id 는 커리큘럼 ID
 *
 * 문서 모양은 배포가 읽는 원본 모양(시스템 설계 "원본 읽기" 4.1)을 그대로 따른다.
 * 엔티티 클래스 대신 Document 로 직접 만든다. Spring 은 안쪽의 id 칸을 _id 로 바꿔 저장해서, 원본 모양이 깨진다
 */
@Component
public class CurriculumVersionRepositoryImpl implements CurriculumVersionRepository {

    private static final String VERSIONS = "curriculum_versions";
    private static final String PUBLISHED = "published_curricula";

    private final MongoTemplate mongoTemplate;
    private final TransactionTemplate transactionTemplate;

    public CurriculumVersionRepositoryImpl(MongoTemplate mongoTemplate, PlatformTransactionManager transactionManager) {
        this.mongoTemplate = mongoTemplate;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * @Transactional 대신 TransactionTemplate 을 쓴다. Mongo 는 트랜잭션 안에서 쓰기 오류(같은 판 번호)가 나면
     * 그 트랜잭션을 바로 끝낸다. 그래서 오류는 트랜잭션이 되돌려진 뒤, 바깥에서 잡는다
     */
    @Override
    public boolean publish(CurriculumVersion version) {
        Document curriculum = toCurriculum(version);
        try {
            // 두 쓰기를 한 트랜잭션으로 묶는다. 둘 다 써지거나 둘 다 안 써진다
            transactionTemplate.executeWithoutResult(status -> {
                // 1. 창고에 판을 쌓는다. 같은 판 번호를 두 번 넣으면 _id 가 겹쳐 Mongo 가 막는다
                mongoTemplate.insert(new Document("_id", version.curriculumId() + ":" + version.version())
                        .append("curriculumId", version.curriculumId())
                        .append("version", version.version())
                        .append("publishedAt", Date.from(version.publishedAt()))
                        .append("basedOn", toBasedOn(version.basedOn()))
                        .append("curriculum", curriculum), VERSIONS);

                // 2. 진열대를 이 판으로 갈아 끼운다(있으면 덮어쓰고, 없으면 넣는다)
                mongoTemplate.save(new Document("_id", version.curriculumId())
                        .append("version", version.version())
                        .append("publishedAt", Date.from(version.publishedAt()))
                        .append("curriculum", curriculum), PUBLISHED);
            });
            return true;
        } catch (DuplicateKeyException e) {
            // 둘이 동시에 공개해서 같은 판 번호가 났다. 트랜잭션이 되돌려진 뒤라 두 곳 다 아무것도 써지지 않았다
            return false;
        }
    }

    @Override
    public Optional<CurriculumVersion> findLast(String curriculumId) {
        Query query = Query.query(Criteria.where("curriculumId").is(curriculumId))
                .with(Sort.by(Sort.Direction.DESC, "version"))
                .limit(1);
        return Optional.ofNullable(mongoTemplate.findOne(query, Document.class, VERSIONS))
                .map(CurriculumVersionRepositoryImpl::toVersion);
    }

    // ─── 판 → 원본 문서 모양. 단계마다 하나씩 내려간다 ───

    private static Document toCurriculum(CurriculumVersion v) {
        return new Document("curriculumId", v.curriculumId())
                .append("title", v.title())
                .append("description", v.description())
                .append("sortOrder", v.sortOrder())
                .append("grade", v.grades().stream().map(CurriculumVersionRepositoryImpl::toGrade).toList());
    }

    private static Document toGrade(CurriculumVersion.Grade g) {
        return node(g.id(), g.title(), g.description())
                .append("semester", g.semesters().stream().map(CurriculumVersionRepositoryImpl::toSemester).toList());
    }

    private static Document toSemester(CurriculumVersion.Semester s) {
        return node(s.id(), s.title(), s.description())
                .append("subject", s.subjects().stream().map(CurriculumVersionRepositoryImpl::toSubject).toList());
    }

    private static Document toSubject(CurriculumVersion.Subject s) {
        return node(s.id(), s.title(), s.description())
                .append("unit", s.units().stream().map(CurriculumVersionRepositoryImpl::toUnit).toList());
    }

    private static Document toUnit(CurriculumVersion.Unit u) {
        return node(u.id(), u.title(), u.description())
                .append("lesson", u.lessons().stream().map(CurriculumVersionRepositoryImpl::toLesson).toList());
    }

    private static Document toLesson(CurriculumVersion.Lesson l) {
        return node(l.id(), l.title(), l.description())
                .append("lessonNumber", l.lessonNumber())
                .append("learningLevel", l.learningLevel());
    }

    private static Document node(String id, String title, String description) {
        // 원본 모양은 안쪽 ID 칸 이름이 _id 가 아니라 id 다
        return new Document("id", id).append("title", title).append("description", description);
    }

    private static Document toBasedOn(CurriculumVersion.BasedOn basedOn) {
        return new Document("skeletonRevision", basedOn.skeletonRevision())
                .append("subjectRevisions", new Document(Map.copyOf(basedOn.subjectRevisions())));
    }

    // ─── 원본 문서 모양 → 판 ───

    private static CurriculumVersion toVersion(Document d) {
        Document c = d.get("curriculum", Document.class);
        return new CurriculumVersion(
                d.getString("curriculumId"),
                d.getInteger("version"),
                c.getString("title"),
                c.getString("description"),
                c.getInteger("sortOrder", 0),
                children(c, "grade").stream().map(CurriculumVersionRepositoryImpl::fromGrade).toList(),
                fromBasedOn(d.get("basedOn", Document.class)),
                d.getDate("publishedAt").toInstant());
    }

    private static CurriculumVersion.Grade fromGrade(Document g) {
        return new CurriculumVersion.Grade(g.getString("id"), g.getString("title"), g.getString("description"),
                children(g, "semester").stream().map(CurriculumVersionRepositoryImpl::fromSemester).toList());
    }

    private static CurriculumVersion.Semester fromSemester(Document s) {
        return new CurriculumVersion.Semester(s.getString("id"), s.getString("title"), s.getString("description"),
                children(s, "subject").stream().map(CurriculumVersionRepositoryImpl::fromSubject).toList());
    }

    private static CurriculumVersion.Subject fromSubject(Document s) {
        return new CurriculumVersion.Subject(s.getString("id"), s.getString("title"), s.getString("description"),
                children(s, "unit").stream().map(CurriculumVersionRepositoryImpl::fromUnit).toList());
    }

    private static CurriculumVersion.Unit fromUnit(Document u) {
        return new CurriculumVersion.Unit(u.getString("id"), u.getString("title"), u.getString("description"),
                children(u, "lesson").stream().map(CurriculumVersionRepositoryImpl::fromLesson).toList());
    }

    private static CurriculumVersion.Lesson fromLesson(Document l) {
        return new CurriculumVersion.Lesson(l.getString("id"), l.getString("title"), l.getString("description"),
                l.getInteger("lessonNumber"), l.getInteger("learningLevel"));
    }

    private static CurriculumVersion.BasedOn fromBasedOn(Document b) {
        Document revisions = b.get("subjectRevisions", Document.class);
        Map<String, Integer> subjectRevisions = new HashMap<>();
        revisions.forEach((subjectId, revision) -> subjectRevisions.put(subjectId, (Integer) revision));
        return new CurriculumVersion.BasedOn(b.getInteger("skeletonRevision"), Map.copyOf(subjectRevisions));
    }

    private static List<Document> children(Document parent, String key) {
        return parent.getList(key, Document.class, List.of());
    }
}
