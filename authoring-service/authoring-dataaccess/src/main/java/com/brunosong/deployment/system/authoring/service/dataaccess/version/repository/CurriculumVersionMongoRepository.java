package com.brunosong.deployment.system.authoring.service.dataaccess.version.repository;

import com.brunosong.deployment.system.authoring.service.dataaccess.version.entity.CurriculumVersionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/** 창고(curriculum_versions). JPA 의 Repository 와 같은 방식으로 메서드 이름에서 쿼리를 만든다 */
public interface CurriculumVersionMongoRepository extends MongoRepository<CurriculumVersionEntity, String> {

    /** 가장 최근 판 하나: curriculumId 가 같은 것 중 version 이 가장 큰 것 */
    Optional<CurriculumVersionEntity> findTopByCurriculumIdOrderByVersionDesc(String curriculumId);

    /**
     * 판 기록: 최근 판이 먼저. 판의 트리(curriculum)는 커서(문서 하나가 수 MB) 빼고 읽는다.
     * fields 의 curriculum: 0 이 "이 칸은 읽지 않는다"는 뜻이다(SQL 에서 고를 칼럼만 적는 것과 같다)
     */
    @Query(value = "{ 'curriculumId': ?0 }", fields = "{ 'curriculum': 0 }", sort = "{ 'version': -1 }")
    List<CurriculumVersionEntity> findSummariesByCurriculumId(String curriculumId);
}
