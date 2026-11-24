package com.brunosong.deployment.system.authoring.service.dataaccess.draft.repository;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.GradeEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SkeletonDraftEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

/**
 * skeleton_drafts 컬렉션. MongoTemplate 대신 Spring Data 의 Repository 로 쓴다(JPA 와 같은 방식).
 * findById, insert, findAll(Pageable) 같은 기본 메서드는 MongoRepository 가 이미 갖고 있다
 */
public interface SkeletonDraftMongoRepository extends MongoRepository<SkeletonDraftEntity, String> {

    /**
     * 목록 검색: ID(_id)나 제목에 pattern 이 들어 있는 것. "i" 는 대소문자를 가리지 않는다.
     * 반환형이 Page 라서 Spring 이 같은 조건으로 전체 개수(count)도 따로 세어 준다
     */
    @Query("{ '$or': [ { '_id': { '$regex': ?0, '$options': 'i' } }, { 'title': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<SkeletonDraftEntity> search(String pattern, Pageable pageable);

    /**
     * 낙관적 락의 조건부 갱신. @Query 가 "어느 문서를"(이 커리큘럼이고 revision 이 내가 본 값일 때만),
     * @Update 가 "무엇으로"($set) 바꿀지 정한다. ?0, ?1 … 은 메서드 인자 순서다.
     * 돌려주는 값은 바뀐 문서 수다. 0 이면 조건에 맞는 문서가 없었다(누가 먼저 고쳤다)
     */
    @Query("{ '_id': ?0, 'revision': ?1 }")
    @Update("{ '$set': { 'title': ?2, 'description': ?3, 'sortOrder': ?4, 'grades': ?5, 'revision': ?6 } }")
    long updateIfRevision(String curriculumId, int expectedRevision, String title, String description, int sortOrder,
                          List<GradeEntity> grades, int newRevision);
}
