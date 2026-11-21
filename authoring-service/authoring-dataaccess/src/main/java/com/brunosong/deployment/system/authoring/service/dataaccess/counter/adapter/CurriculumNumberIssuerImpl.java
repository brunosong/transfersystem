package com.brunosong.deployment.system.authoring.service.dataaccess.counter.adapter;

import com.brunosong.deployment.system.authoring.service.ports.output.repository.CurriculumNumberIssuer;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * counters 컬렉션의 번호표 문서 { _id: "curriculum", seq: 7 } 에서 번호를 1 올려 가져온다.
 * 올리기와 가져오기가 한 번의 명령이라, 둘이 동시에 불러도 같은 번호를 받지 않는다
 */
@Component
@RequiredArgsConstructor
public class CurriculumNumberIssuerImpl implements CurriculumNumberIssuer {

    private static final String COLLECTION = "counters";
    private static final String COUNTER_ID = "curriculum";

    private final MongoTemplate mongoTemplate;

    @Override
    public long next() {
        Document counter = mongoTemplate.findAndModify(
                // 커리큘럼 번호표를 찾는다.
                Query.query(Criteria.where("_id").is(COUNTER_ID)),
                // seq를 1 올린다.
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Document.class,
                COLLECTION);

        // 처음 만들때 Mongo는 seq를 int로 넣는다. 어느 쪽이든 long으로 바꾼다.
        return ((Number)counter.get("seq")).longValue();

    }
}
