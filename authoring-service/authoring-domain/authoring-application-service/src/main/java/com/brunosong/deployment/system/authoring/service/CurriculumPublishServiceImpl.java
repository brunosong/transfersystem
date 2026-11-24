package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.PublishRejectedException;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
import com.brunosong.deployment.system.authoring.service.ports.input.service.CurriculumPublishService;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.CurriculumVersionRepository;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SubjectDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 공개. 쪼개 둔 애그리거트(뼈대, 과목)를 다시 모아 판 하나로 만든다(ADR 0009 "공개가 어려워진다").
 * 초안 읽기는 트랜잭션에 넣지 않는다. 읽은 revision 이 화면이 본 것과 같으면, 읽은 내용이 곧 화면이 본 내용이다
 */
@Service
@RequiredArgsConstructor
public class CurriculumPublishServiceImpl implements CurriculumPublishService {

    private final SkeletonDraftRepository skeletonDraftRepository;
    private final SubjectDraftRepository subjectDraftRepository;
    private final CurriculumVersionRepository curriculumVersionRepository;

    @Override
    public CurriculumVersion publish(String curriculumId, int skeletonRevision, Map<String, Integer> subjectRevisions) {
        // 1. 초안을 모두 읽는다
        SkeletonDraft skeleton = skeletonDraftRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));
        List<SubjectDraft> subjects = subjectDraftRepository.findByCurriculumId(curriculumId);

        // 2. 화면이 본 것과 지금이 같은지 본다(공개에 거는 낙관적 락). 과목이 늘거나 줄어도 다르다
        Map<String, Integer> current = subjects.stream()
                .collect(Collectors.toMap(SubjectDraft::getId, SubjectDraft::getRevision));
        if (skeleton.getRevision() != skeletonRevision || !current.equals(subjectRevisions)) {
            throw new RevisionConflictException(curriculumId, skeletonRevision);
        }

        // 3. 직전 판과 같은 초안으로 또 공개하려 하면 거절한다(바뀐 것이 없다, 불변식 5)
        Optional<CurriculumVersion> last = curriculumVersionRepository.findLast(curriculumId);
        CurriculumVersion.BasedOn now = new CurriculumVersion.BasedOn(skeletonRevision, current);
        if (last.isPresent() && last.get().basedOn().equals(now)) {
            throw new PublishRejectedException("판 " + last.get().version() + " 뒤로 바뀐 것이 없다");
        }

        // 4. 판을 조립하고(어긋나면 PublishRejectedException) 두 곳에 쓴다. 판 번호는 직전 + 1
        int version = last.map(v -> v.version() + 1).orElse(1);
        CurriculumVersion published = CurriculumVersion.assemble(version, skeleton, subjects, Instant.now());
        if (!curriculumVersionRepository.publish(published)) {
            // 그사이 누가 같은 판 번호로 먼저 공개했다
            throw new RevisionConflictException(curriculumId, skeletonRevision);
        }
        return published;
    }
}
