package com.brunosong.deployment.system.authoring.service.dataaccess.draft.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SubjectDraftEntity;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SubjectDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

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
                draft.getRevision());

        mongoTemplate.insert(entity);
    }

    @Override
    public List<SubjectDraft> findByCurriculumId(String curriculumId) {
        // 만든 순서를 알 칸이 아직 없어서 제목 순으로 읽는다.
        Query query = Query.query(Criteria.where("curriculumId").is(curriculumId)).with(Sort.by("title"));
        return mongoTemplate.find(query, SubjectDraftEntity.class).stream()
                .map(e -> SubjectDraft.restore(
                                        e.getId(),
                                        e.getCurriculumId(),
                                        e.getSemesterId(),
                                        e.getTitle(),
                                        e.getDescription(),
                                        e.getRevision())).toList();
    }
}
