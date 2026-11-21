package com.brunosong.deployment.system.authoring.service.dataaccess.draft.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SkeletonDraftEntity;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** 뼈대 초안을 skeleton_drafts 컬렉션에 넣는다 */
@Component
@RequiredArgsConstructor
public class SkeletonDraftRepositoryImpl implements SkeletonDraftRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(SkeletonDraft draft) {
        // 도메인 객체를 문서 모양으로 바꾼다.
        SkeletonDraftEntity entity = new SkeletonDraftEntity(
                draft.getCurriculumId(),
                draft.getTitle(),
                draft.getDescription(),
                draft.getRevision()
        );

        // 새로 넣기만 한다. 같은 _id (커리큘럼 ID)가 이미 있으면 Mongo가 오류를 낸다.
        mongoTemplate.insert(entity);
    }

    @Override
    public Optional<SkeletonDraft> findById(String curriculumId) {
        // _id로 문서 하나를 읽는다. 없으면 null 이 온다.
        SkeletonDraftEntity entity = mongoTemplate.findById(curriculumId, SkeletonDraftEntity.class);

        // 문서를 도메인 객체로 되돌린다. 저장된 revision 을 그대로 쓴다.
        return Optional.ofNullable(entity)
                .map(e -> SkeletonDraft.restore(
                        e.getCurriculumId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getRevision()));

    }

    @Override
    public boolean update(SkeletonDraft edited, int expectedRevision) {
        UpdateResult result = mongoTemplate.updateFirst(
                // 조건 : 이 커리큘럼이고, revision 이 내가 본 값일 때만
                Query.query(Criteria.where("_id").is(edited.getCurriculumId())
                        .and("revision").is(expectedRevision)),
                // 고친 내용과 새 revision을 넣는다.
                new Update()
                        .set("title", edited.getTitle())
                        .set("description", edited.getDescription())
                        .set("revision", edited.getRevision()),
                SkeletonDraftEntity.class);

        // 조건에 맞는 문서가 없었다. = revision이 이미 바뀌었다.
        return result.getMatchedCount() == 1;
    }
}
