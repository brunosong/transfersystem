package com.brunosong.deployment.system.authoring.service.dataaccess.draft.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SubjectDraftEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.mapper.SubjectDraftDataAccessMapper;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
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

/** 과목 초안을 subject_drafts 컬렉션에 넣고 읽는다. 모양 바꾸기는 SubjectDraftDataAccessMapper 가 한다 */
@Component
@RequiredArgsConstructor
public class SubjectDraftRepositoryImpl implements SubjectDraftRepository {

    private final MongoTemplate mongoTemplate;
    private final SubjectDraftDataAccessMapper mapper;

    @Override
    public void save(SubjectDraft draft) {
        // 새로 넣기만 한다. 같은 _id 가 있으면 Mongo 가 오류를 낸다
        mongoTemplate.insert(mapper.toEntity(draft));
    }

    @Override
    public List<SubjectDraft> findByCurriculumId(String curriculumId) {
        Query query = Query.query(Criteria.where("curriculumId").is(curriculumId)).with(Sort.by("sortOrder", "title"));
        return mongoTemplate.find(query, SubjectDraftEntity.class).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<SubjectDraft> findById(String subjectId) {
        return Optional.ofNullable(mongoTemplate.findById(subjectId, SubjectDraftEntity.class))
                .map(mapper::toDomain);
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
                        .set("sortOrder", draft.getSortOrder())
                        .set("units", mapper.toUnitEntities(draft.getUnits()))
                        .set("revision", draft.getRevision()),
                SubjectDraftEntity.class);
        return result.getMatchedCount() == 1;
    }

    @Override
    public boolean delete(String subjectId, int expectedRevision) {
        // 고치기와 같은 조건이다. 남이 방금 고친 과목을 보지 못한 채 지우지 않게 한다
        return mongoTemplate.remove(
                Query.query(Criteria.where("_id").is(subjectId).and("revision").is(expectedRevision)),
                SubjectDraftEntity.class).getDeletedCount() == 1;
    }
}
