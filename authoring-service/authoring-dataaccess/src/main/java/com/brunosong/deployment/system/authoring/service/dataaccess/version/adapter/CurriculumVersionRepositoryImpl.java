package com.brunosong.deployment.system.authoring.service.dataaccess.version.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.version.entity.PublishedCurriculumEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.version.mapper.CurriculumVersionDataAccessMapper;
import com.brunosong.deployment.system.authoring.service.dataaccess.version.repository.CurriculumVersionMongoRepository;
import com.brunosong.deployment.system.authoring.service.dataaccess.version.repository.PublishedCurriculumMongoRepository;
import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.CurriculumVersionRepository;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.VersionSummary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 판을 두 곳에 쓴다. Spring Data 의 MongoRepository 를 쓴다(JPA 의 Repository 와 같은 방식).
 * - 창고(curriculum_versions): 모든 판을 쌓는다. _id 는 "CUR003:2"
 * - 진열대(published_curricula): 커리큘럼마다 최신 판 하나. 배포가 읽는다. _id 는 커리큘럼 ID
 * 모양 바꾸기는 CurriculumVersionDataAccessMapper 가 한다
 */
@Component
public class CurriculumVersionRepositoryImpl implements CurriculumVersionRepository {

    private final CurriculumVersionMongoRepository versions;
    private final PublishedCurriculumMongoRepository published;
    private final CurriculumVersionDataAccessMapper mapper;
    private final TransactionTemplate transactionTemplate;

    public CurriculumVersionRepositoryImpl(CurriculumVersionMongoRepository versions,
                                           PublishedCurriculumMongoRepository published,
                                           CurriculumVersionDataAccessMapper mapper,
                                           PlatformTransactionManager transactionManager) {
        this.versions = versions;
        this.published = published;
        this.mapper = mapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * @Transactional 대신 TransactionTemplate 을 쓴다. Mongo 는 트랜잭션 안에서 쓰기 오류(같은 판 번호)가 나면
     * 그 트랜잭션을 바로 끝낸다. 그래서 오류는 트랜잭션이 되돌려진 뒤, 바깥에서 잡는다
     */
    @Override
    public boolean publish(CurriculumVersion version) {
        try {
            // 두 쓰기를 한 트랜잭션으로 묶는다. 둘 다 써지거나 둘 다 안 써진다
            transactionTemplate.executeWithoutResult(status -> {
                // 1. 창고에 판을 쌓는다. insert 는 같은 _id 가 있으면 덮어쓰지 않고 거절한다(DuplicateKeyException)
                versions.insert(mapper.toVersionEntity(version));
                // 2. 진열대를 이 판으로 갈아 끼운다. save 는 같은 _id 가 있으면 덮어쓰고, 없으면 넣는다
                published.save(mapper.toPublishedEntity(version));
            });
            return true;
        } catch (DuplicateKeyException e) {
            // 둘이 동시에 공개해서 같은 판 번호가 났다. 트랜잭션이 되돌려진 뒤라 두 곳 다 아무것도 써지지 않았다
            return false;
        }
    }

    @Override
    public Optional<CurriculumVersion> findLast(String curriculumId) {
        return versions.findTopByCurriculumIdOrderByVersionDesc(curriculumId).map(mapper::toDomain);
    }

    @Override
    public List<VersionSummary> findSummaries(String curriculumId) {
        return versions.findSummariesByCurriculumId(curriculumId).stream().map(mapper::toSummary).toList();
    }

    @Override
    public Map<String, Integer> lastVersionNumbers(List<String> curriculumIds) {
        return published.findVersionsByIdIn(curriculumIds).stream()
                .collect(Collectors.toMap(PublishedCurriculumEntity::id, PublishedCurriculumEntity::version));
    }
}
