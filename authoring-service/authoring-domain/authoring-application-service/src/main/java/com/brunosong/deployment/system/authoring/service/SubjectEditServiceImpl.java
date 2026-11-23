package com.brunosong.deployment.system.authoring.service;

import com.brunosong.deployment.system.authoring.service.entity.Lesson;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.entity.Unit;
import com.brunosong.deployment.system.authoring.service.exception.RevisionConflictException;
import com.brunosong.deployment.system.authoring.service.exception.SubjectNotFoundException;
import com.brunosong.deployment.system.authoring.service.ports.input.service.SubjectEditService;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SubjectDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/** 과목 초안 고치기. 뼈대 고치기(CurriculumEditServiceImpl)와 같은 흐름이지만 과목의 revision 을 쓴다 */
@Service
@RequiredArgsConstructor
public class SubjectEditServiceImpl implements SubjectEditService {

    private final SubjectDraftRepository subjectDraftRepository;

    @Override
    public AddedUnit addUnit(String curriculumId, String subjectId, String title, String description,
                             int expectedRevision) {
        Unit unit = Unit.create(title, description);
        SubjectDraft subject = change(curriculumId, subjectId, expectedRevision, s -> s.addUnit(unit));
        return new AddedUnit(unit, subject.getRevision());
    }

    @Override
    public AddedLesson addLesson(String curriculumId, String subjectId, String unitId, String title,
                                 String description, int expectedRevision) {
        Lesson lesson = Lesson.create(title, description);
        SubjectDraft subject = change(curriculumId, subjectId, expectedRevision, s -> s.addLesson(unitId, lesson));
        return new AddedLesson(lesson, subject.getRevision());
    }

    @Override
    public void removeSubject(String curriculumId, String subjectId, int expectedRevision) {
        // 지우기도 고치기와 같은 검사(없으면 404, 예전 revision 이면 409)를 거친다
        load(curriculumId, subjectId, expectedRevision);
        if (!subjectDraftRepository.delete(subjectId, expectedRevision)) {
            throw new RevisionConflictException(subjectId, expectedRevision);
        }
    }

    @Override
    public SubjectDraft removeUnit(String curriculumId, String subjectId, String unitId, int expectedRevision) {
        return change(curriculumId, subjectId, expectedRevision, s -> s.removeUnit(unitId));
    }

    @Override
    public SubjectDraft removeLesson(String curriculumId, String subjectId, String unitId, String lessonId,
                                     int expectedRevision) {
        return change(curriculumId, subjectId, expectedRevision, s -> s.removeLesson(unitId, lessonId));
    }

    @Override
    public SubjectDraft editSubject(String curriculumId, String subjectId, String title, String description, int expectedRevision) {
        return change(curriculumId, subjectId, expectedRevision, draft -> draft.edit(title, description));
    }

    @Override
    public SubjectDraft editUnit(String curriculumId, String subjectId, String unitId, String title, String description, int expectedRevision) {
        return change(curriculumId, subjectId, expectedRevision, draft -> draft.editUnit(unitId, title, description));
    }

    @Override
    public SubjectDraft editLesson(String curriculumId, String subjectId, String unitId, String lessonId, String title,
                                   String description, int expectedRevision) {
        return change(curriculumId, subjectId, expectedRevision,
                draft -> draft.editLesson(unitId, lessonId, title, description));
    }

    /**
     * 모든 과목 고치기가 거치는 흐름. 뼈대의 change 와 같다.
     * 1. 과목을 읽는다. 없거나 다른 커리큘럼의 과목이면 404
     * 2. 화면이 본 revision 과 지금 revision 이 다르면 바로 409
     * 3. 넘겨받은 고치기를 루트(SubjectDraft)에 한다
     * 4. 화면이 본 revision 일 때만 저장한다. 아니면 409(읽은 뒤 저장하기 전에 누가 끼어든 경우)
     */
    private SubjectDraft change(String curriculumId, String subjectId, int expectedRevision,
                                Consumer<SubjectDraft> edit) {
        SubjectDraft subject = load(curriculumId, subjectId, expectedRevision);

        edit.accept(subject);

        if (!subjectDraftRepository.update(subject, expectedRevision)) {
            throw new RevisionConflictException(subjectId, expectedRevision);
        }
        return subject;
    }

    /** 과목을 읽고 확인한다. 없거나 다른 커리큘럼의 과목이면 404, 화면이 본 revision 이 아니면 409 */
    private SubjectDraft load(String curriculumId, String subjectId, int expectedRevision) {
        SubjectDraft subject = subjectDraftRepository.findById(subjectId)
                // URL 의 커리큘럼과 과목이 맞지 않으면 없는 것으로 본다. 남의 커리큘럼 과목을 고치지 않게 한다
                .filter(s -> s.getCurriculumId().equals(curriculumId))
                .orElseThrow(() -> new SubjectNotFoundException(curriculumId, subjectId));

        if (subject.getRevision() != expectedRevision) {
            throw new RevisionConflictException(subjectId, expectedRevision);
        }
        return subject;
    }
}
