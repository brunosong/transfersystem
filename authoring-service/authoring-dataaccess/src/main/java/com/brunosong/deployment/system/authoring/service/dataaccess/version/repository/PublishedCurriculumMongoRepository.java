package com.brunosong.deployment.system.authoring.service.dataaccess.version.repository;

import com.brunosong.deployment.system.authoring.service.dataaccess.version.entity.PublishedCurriculumEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/** 진열대(published_curricula). 배포가 읽는 곳이다 */
public interface PublishedCurriculumMongoRepository extends MongoRepository<PublishedCurriculumEntity, String> {

    /** 여러 커리큘럼의 최신 판 번호만 읽는다. 목록 화면에 쓴다. 트리(curriculum)는 읽지 않는다 */
    @Query(value = "{ '_id': { '$in': ?0 } }", fields = "{ 'version': 1, 'publishedAt': 1 }")
    List<PublishedCurriculumEntity> findVersionsByIdIn(List<String> curriculumIds);
}
