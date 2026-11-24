package com.brunosong.deployment.system.authoring.service.dataaccess.draft.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SkeletonDraftEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.mapper.SkeletonDraftDataAccessMapper;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftPage;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
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
import java.util.regex.Pattern;

/** 뼈대 초안을 skeleton_drafts 컬렉션에 넣고 읽는다. 모양 바꾸기는 SkeletonDraftDataAccessMapper 가 한다 */
@Component
@RequiredArgsConstructor
public class SkeletonDraftRepositoryImpl implements SkeletonDraftRepository {

    private final MongoTemplate mongoTemplate;
    private final SkeletonDraftDataAccessMapper mapper;

    @Override
    public void save(SkeletonDraft draft) {
        // 새로 넣기만 한다. 같은 _id (커리큘럼 ID)가 이미 있으면 Mongo가 오류를 낸다.
        mongoTemplate.insert(mapper.toEntity(draft));
    }

    @Override
    public Optional<SkeletonDraft> findById(String curriculumId) {
        SkeletonDraftEntity entity = mongoTemplate.findById(curriculumId, SkeletonDraftEntity.class);
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public SkeletonDraftPage findPage(String keyword, int page, int size) {
        // 1. 조건: keyword 가 ID(_id)나 제목에 들어 있으면, 비었으면 조건 없이 전부
        Query query = new Query();
        if (keyword != null && !keyword.isBlank()) {
            // 사람이 친 글자를 정규식 기호로 읽지 않게 감싼다. "i"는 대소문자를 가리지 않는다.
            String pattern = Pattern.quote(keyword.trim());
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("_id").regex(pattern, "i"),
                    Criteria.where("title").regex(pattern, "i")
            ));
        }

        // 2. 전체 개수를 먼저 센다. 쪽을 자르기 전에 세야 전체가 나온다.
        long total = mongoTemplate.count(query, SkeletonDraftEntity.class);

        // 3. 정렬 순서대로, 같으면 ID 순으로 그 쪽만 읽는다
        query.with(Sort.by("sortOrder", "_id")).skip((long) page * size).limit(size);
        List<SkeletonDraft> items = mongoTemplate.find(query, SkeletonDraftEntity.class).stream()
                .map(mapper::toDomain)
                .toList();

        return new SkeletonDraftPage(items, total);
    }

    @Override
    public boolean update(SkeletonDraft draft, int expectedRevision) {
        UpdateResult result = mongoTemplate.updateFirst(
                // 조건 : 이 커리큘럼이고, revision 이 내가 본 값일 때만
                Query.query(Criteria.where("_id").is(draft.getCurriculumId())
                        .and("revision").is(expectedRevision)),
                // 고친 내용과 새 revision을 넣는다.
                new Update()
                        .set("title", draft.getTitle())
                        .set("description", draft.getDescription())
                        .set("sortOrder", draft.getSortOrder())
                        .set("grades", mapper.toGradeEntities(draft.getGrades()))
                        .set("revision", draft.getRevision()),
                SkeletonDraftEntity.class);

        // 조건에 맞는 문서가 없었다. = revision이 이미 바뀌었다.
        return result.getMatchedCount() == 1;
    }
}
