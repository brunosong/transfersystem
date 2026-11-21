package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import com.brunosong.deployment.system.authoring.service.entity.Grade;
import com.brunosong.deployment.system.authoring.service.entity.Semester;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 뼈대 초안 고치기(제목·설명, 학년 추가, 학기 추가). 낙관적 락으로 남의 수정을 덮어쓰지 않는다 */
@Service
@RequiredArgsConstructor
public class CurriculumEditService {

    private final SkeletonDraftRepository skeletonDraftRepository;

    public SkeletonDraft edit(String curriculumId, String title, String description, int expectedRevision) {
        // 1. 지금 초안을 읽는다. 없으면 404
        SkeletonDraft current = skeletonDraftRepository
                .findById(curriculumId).orElseThrow(() -> new CurriculumNotFoundException(curriculumId));

        // 2. 도메인이 고친다. 제목 검사와 revision + 1은 도메인이 한다.
        current.edit(title, description);

        // 3. 내가 본 revision 일 때만 저장한다. 아니면 409
        // 1번에서 읽은 revision 이 아니라 화면이 보낸 revision 으로 확인한다.
        // 화면을 연 뒤로 누가 고쳤는지가 궁금한 것이기 때문이다.
        if (!skeletonDraftRepository.update(current, expectedRevision)) {
            throw new RevisionConflictException(curriculumId, expectedRevision);
        }
        return current;
    }

    /**
     * 학년을 하나 붙인다. 뼈대를 고치는 일이라 제목 고치기와 같은 낙관적 락을 쓴다.
     * 새 학년의 ID와 새 revision 을 돌려준다. 화면은 둘 다 들고 있어야 다음에 또 고칠 수 있다
     */
    public AddedGrade addGrade(String curriculumId, String title, String description, int expectedRevision) {
        // 1. 지금 초안을 읽는다. 없으면 404
        SkeletonDraft current = skeletonDraftRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));

        // 2. 학년을 만들고(ID는 도메인이 만든다) 초안에 붙인다. revision 이 1 오른다
        Grade grade = Grade.create(title, description);
        current.addGrade(grade);

        // 3. 화면이 본 revision 일 때만 저장한다. 아니면 409
        if (!skeletonDraftRepository.update(current, expectedRevision)) {
            throw new RevisionConflictException(curriculumId, expectedRevision);
        }
        return new AddedGrade(grade, current.getRevision());
    }

    /** 학년 추가의 결과. 새 학년과 저장된 뒤의 revision */
    public record AddedGrade(Grade grade, int revision) {
    }

    /**
     * 학년 하나에 학기를 붙인다. 학기도 뼈대 안에 있어서 같은 낙관적 락을 쓴다.
     * 학년이 없으면 도메인이 GradeNotFoundException 을 던진다(404)
     */
    public AddedSemester addSemester(String curriculumId, String gradeId, String title, String description,
                                     int expectedRevision) {
        // 1. 지금 초안을 읽는다. 없으면 404
        SkeletonDraft current = skeletonDraftRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumNotFoundException(curriculumId));

        // 2. 학기를 만들고 그 학년에 붙인다. 학년이 있는지는 도메인이 본다
        Semester semester = Semester.create(title, description);
        current.addSemester(gradeId, semester);

        // 3. 화면이 본 revision 일 때만 저장한다. 아니면 409
        if (!skeletonDraftRepository.update(current, expectedRevision)) {
            throw new RevisionConflictException(curriculumId, expectedRevision);
        }
        return new AddedSemester(semester, current.getRevision());
    }

    /** 학기 추가의 결과. 새 학기와 저장된 뒤의 revision */
    public record AddedSemester(Semester semester, int revision) {
    }
}
