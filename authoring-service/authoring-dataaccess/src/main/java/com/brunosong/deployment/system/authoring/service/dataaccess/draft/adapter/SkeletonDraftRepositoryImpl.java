package com.brunosong.deployment.system.authoring.service.dataaccess.draft.adapter;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SkeletonDraftEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.mapper.SkeletonDraftDataAccessMapper;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.repository.SkeletonDraftMongoRepository;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftPage;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 뼈대 초안을 skeleton_drafts 컬렉션에 넣고 읽는다. Spring Data 의 Repository(SkeletonDraftMongoRepository)를 쓴다.
 * 모양 바꾸기는 SkeletonDraftDataAccessMapper 가 한다
 */
@Component
@RequiredArgsConstructor
public class SkeletonDraftRepositoryImpl implements SkeletonDraftRepository {

    private final SkeletonDraftMongoRepository repository;
    private final SkeletonDraftDataAccessMapper mapper;

    @Override
    public void save(SkeletonDraft draft) {
        // save 가 아니라 insert 다. Repository 의 save 는 같은 _id 가 있으면 말없이 덮어쓴다.
        // insert 는 같은 _id(커리큘럼 ID)가 있으면 거절한다
        repository.insert(mapper.toEntity(draft));
    }

    @Override
    public Optional<SkeletonDraft> findById(String curriculumId) {
        return repository.findById(curriculumId).map(mapper::toDomain);
    }

    @Override
    public SkeletonDraftPage findPage(String keyword, int page, int size) {
        // 정렬 순서대로, 같으면 ID 순으로. curriculumId 는 문서의 _id 라 Spring 이 _id 로 바꿔 정렬한다
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder", "curriculumId"));

        // keyword 가 비면 전부, 있으면 ID나 제목으로 찾는다. 사람이 친 글자를 정규식 기호로 읽지 않게 감싼다
        Page<SkeletonDraftEntity> found = (keyword == null || keyword.isBlank())
                ? repository.findAll(pageable)
                : repository.search(Pattern.quote(keyword.trim()), pageable);

        // Page 는 그 쪽의 목록과 전체 개수를 함께 갖고 있다(MongoTemplate 에서는 count 를 따로 불렀다)
        return new SkeletonDraftPage(found.map(mapper::toDomain).getContent(), found.getTotalElements());
    }

    @Override
    public boolean update(SkeletonDraft draft, int expectedRevision) {
        // 조건(revision 이 내가 본 값)과 갱신을 한 번의 명령으로 보낸다. 바뀐 문서가 1 이면 성공, 0 이면 누가 먼저 고쳤다
        long modified = repository.updateIfRevision(
                draft.getCurriculumId(), expectedRevision,
                draft.getTitle(), draft.getDescription(), draft.getSortOrder(),
                mapper.toGradeEntities(draft.getGrades()), draft.getRevision());
        return modified == 1;
    }
}
