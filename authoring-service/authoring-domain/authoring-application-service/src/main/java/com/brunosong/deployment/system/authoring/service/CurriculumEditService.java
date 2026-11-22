package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.exception.CurriculumNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftRepository;
import com.brunosong.deployment.system.authoring.service.entity.Grade;
import com.brunosong.deployment.system.authoring.service.entity.Semester;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 뼈대 초안 고치기. 커리큘럼 제목·설명, 학년과 학기의 추가와 수정.
 * 모두 같은 흐름이다: 읽고 → 루트로 고치고 → 화면이 본 revision 일 때만 저장한다(낙관적 락).
 * 그 흐름은 change 한곳에 두고, 메서드마다 무엇을 고칠지만 넘긴다
 */
@Service
@RequiredArgsConstructor
public class CurriculumEditService {

    private final SkeletonDraftRepository skeletonDraftRepository;

    public SkeletonDraft edit(String curriculumId, String title, String description, int expectedRevision) {
        return change(curriculumId, expectedRevision, draft -> draft.edit(title, description));
    }

    /**
     * 학년을 하나 붙인다. 뼈대를 고치는 일이라 제목 고치기와 같은 낙관적 락을 쓴다.
     * 새 학년의 ID와 새 revision 을 돌려준다. 화면은 둘 다 들고 있어야 다음에 또 고칠 수 있다
     */
    public AddedGrade addGrade(String curriculumId, String title, String description, int expectedRevision) {
        // 학년은 여기서 만든다. 만든 학년의 ID를 돌려줘야 해서 밖에 들고 있는다
        Grade grade = Grade.create(title, description);
        SkeletonDraft draft = change(curriculumId, expectedRevision, d -> d.addGrade(grade));
        return new AddedGrade(grade, draft.getRevision());
    }


    /** 학년에 학기를 붙인다. 학년이 없으면 GradeNotFoundException(404) */
    public AddedSemester addSemester(String curriculumId, String gradeId, String title, String description,
                                     int expectedRevision) {
        Semester semester = Semester.create(title, description);
        SkeletonDraft draft = change(curriculumId, expectedRevision, d -> d.addSemester(gradeId, semester));
        return new AddedSemester(semester, draft.getRevision());
    }

    /** 학년의 제목과 설명을 고친다. 학년이 없으면 GradeNotFoundException(404) */
    public SkeletonDraft editGrade(String curriculumId, String gradeId, String title, String description,
                                   int expectedRevision) {
        return change(curriculumId, expectedRevision, draft -> draft.editGrade(gradeId, title, description));
    }

    /** 학기의 제목과 설명을 고친다. 학년이나 학기가 없으면 각각의 NotFoundException(404) */
    public SkeletonDraft editSemester(String curriculumId, String gradeId, String semesterId, String title,
                                      String description, int expectedRevision) {
        return change(curriculumId, expectedRevision,
                draft -> draft.editSemester(gradeId, semesterId, title, description));
    }


    /**
     * 모든 고치기가 거치는 흐름.
     * 1. 지금 초안을 읽는다. 없으면 404
     * 2. 넘겨받은 고치기를 루트에 한다. 검사와 revision + 1 은 도메인이 한다
     * 3. 화면이 본 revision 일 때만 저장한다. 아니면 409.
     *    방금 읽은 revision 이 아니라 화면이 보낸 revision 으로 확인한다. 화면을 연 뒤로 누가 고쳤는지가 궁금하기 때문이다
     */
    private SkeletonDraft change(String curriculumId, int expectedRevision, Consumer<SkeletonDraft> edit) {
        // 1. 지금 초안을 읽는다. 없으면 404
        SkeletonDraft draft = skeletonDraftRepository
                .findById(curriculumId).orElseThrow(() -> new CurriculumNotFoundException(curriculumId));

        edit.accept(draft);

        if (!skeletonDraftRepository.update(draft, expectedRevision)) {
            throw new RevisionConflictException(curriculumId, expectedRevision);
        }
        return draft;
    }


    /** 학기 추가의 결과. 새 학기와 저장된 뒤의 revision */
    public record AddedSemester(Semester semester, int revision) {
    }

    /** 학년 추가의 결과. 새 학년과 저장된 뒤의 revision */
    public record AddedGrade(Grade grade, int revision) {
    }
}
