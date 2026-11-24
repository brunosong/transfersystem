package com.brunosong.deployment.system.authoring.service.dataaccess.draft.mapper;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.LessonEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SubjectDraftEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.UnitEntity;
import com.brunosong.deployment.system.authoring.service.entity.Lesson;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.entity.Unit;
import org.springframework.stereotype.Component;

import java.util.List;

/** 과목 초안 ↔ subject_drafts 문서 */
@Component
public class SubjectDraftDataAccessMapper {

    public SubjectDraftEntity toEntity(SubjectDraft draft) {
        return new SubjectDraftEntity(draft.getId(), draft.getCurriculumId(), draft.getSemesterId(), draft.getTitle(),
                draft.getDescription(), draft.getSortOrder(), toUnitEntities(draft.getUnits()), draft.getRevision());
    }

    /** 조건부 갱신에서 units 칸만 통째로 바꿀 때 쓴다 */
    public List<UnitEntity> toUnitEntities(List<Unit> units) {
        return units.stream()
                .map(u -> new UnitEntity(u.getId(), u.getTitle(), u.getDescription(),
                        u.getLessons().stream()
                                .map(l -> new LessonEntity(l.getId(), l.getTitle(), l.getDescription(), l.getLearningLevel()))
                                .toList()))
                .toList();
    }

    /** 문서를 도메인 객체로 되돌린다. 저장된 revision 을 그대로 쓴다 */
    public SubjectDraft toDomain(SubjectDraftEntity e) {
        return SubjectDraft.restore(e.getId(), e.getCurriculumId(), e.getSemesterId(), e.getTitle(),
                e.getDescription(), e.getSortOrder() == null ? 0 : e.getSortOrder(), toUnits(e.getUnits()),
                e.getRevision());
    }

    private List<Unit> toUnits(List<UnitEntity> entities) {
        // 단원 칸이 생기기 전에 만든 과목에는 units 가 없어서 null 로 온다
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(e -> Unit.restore(e.id(), e.title(), e.description(), toLessons(e.lessons())))
                .toList();
    }

    private List<Lesson> toLessons(List<LessonEntity> entities) {
        // 차시 칸이 생기기 전에 넣은 단원에는 lessons 가 없어서 null 로 온다
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                // 학습 수준 칸이 생기기 전에 넣은 차시는 null 이라 1로 읽는다
                .map(e -> Lesson.restore(e.id(), e.title(), e.description(),
                        e.learningLevel() == null ? Lesson.MIN_LEARNING_LEVEL : e.learningLevel()))
                .toList();
    }
}
