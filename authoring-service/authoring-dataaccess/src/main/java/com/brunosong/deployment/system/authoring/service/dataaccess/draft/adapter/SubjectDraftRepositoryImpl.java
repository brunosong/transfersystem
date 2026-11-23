package com.brunosong.deployment.system.authoring.service.dataaccess.draft.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.LessonEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SubjectDraftEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.UnitEntity;
import com.brunosong.deployment.system.authoring.service.entity.Lesson;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.entity.Unit;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SubjectDraftRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubjectDraftRepositoryImpl implements SubjectDraftRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(SubjectDraft draft) {

        SubjectDraftEntity entity = new SubjectDraftEntity(
                draft.getId(),
                draft.getCurriculumId(),
                draft.getSemesterId(),
                draft.getTitle(),
                draft.getDescription(),
                draft.getRevision(),
                toUnitEntities(draft.getUnits()));

        mongoTemplate.insert(entity);
    }

    @Override
    public List<SubjectDraft> findByCurriculumId(String curriculumId) {
        Query query = Query.query(Criteria.where("curriculumId").is(curriculumId)).with(Sort.by("title"));
        return mongoTemplate.find(query, SubjectDraftEntity.class).stream()
                .map(SubjectDraftRepositoryImpl::toDomain)
                .toList();
    }

    @Override
    public Optional<SubjectDraft> findById(String subjectId) {
        return Optional.ofNullable(mongoTemplate.findById(subjectId, SubjectDraftEntity.class))
                .map(SubjectDraftRepositoryImpl::toDomain);
    }

    @Override
    public boolean update(SubjectDraft draft, int expectedRevision) {
        UpdateResult result = mongoTemplate.updateFirst(
                // 조건: 이 과목이고, revision 이 화면이 본 값일 때만
                Query.query(Criteria.where("_id").is(draft.getId()).and("revision").is(expectedRevision)),
                new Update()
                        .set("semesterId", draft.getSemesterId())
                        .set("title", draft.getTitle())
                        .set("description", draft.getDescription())
                        .set("units", toUnitEntities(draft.getUnits()))
                        .set("revision", draft.getRevision()),
                SubjectDraftEntity.class);
        return result.getMatchedCount() == 1;
    }

    private static SubjectDraft toDomain(SubjectDraftEntity e) {
        return SubjectDraft.restore(e.getId(), e.getCurriculumId(), e.getSemesterId(), e.getTitle(),
                e.getDescription(), e.getRevision(), toUnits(e.getUnits()));
    }

    private static List<UnitEntity> toUnitEntities(List<Unit> units) {
        return units.stream()
                .map(u -> new UnitEntity(u.getId(), u.getTitle(), u.getDescription(),
                        u.getLessons().stream()
                                .map(l -> new LessonEntity(l.getId(), l.getTitle(), l.getDescription()))
                                .toList()))
                .toList();
    }

    private static List<Unit> toUnits(List<UnitEntity> entities) {
        // 단원 칸이 생기기 전에 만든 과목(Math, 과학)에는 units 가 없어서 null 로 온다
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(e -> Unit.restore(e.id(), e.title(), e.description(), toLessons(e.lessons())))
                .toList();
    }

    private static List<Lesson> toLessons(List<LessonEntity> entities) {
        // 차시 칸이 생기기 전에 넣은 단원(Unit 1)에는 lessons 가 없어서 null 로 온다
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(e -> Lesson.restore(e.id(), e.title(), e.description()))
                .toList();
    }
}
