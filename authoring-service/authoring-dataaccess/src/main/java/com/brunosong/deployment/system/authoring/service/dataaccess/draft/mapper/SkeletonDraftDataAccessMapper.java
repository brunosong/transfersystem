package com.brunosong.deployment.system.authoring.service.dataaccess.draft.mapper;

import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.GradeEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SemesterEntity;
import com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity.SkeletonDraftEntity;
import com.brunosong.deployment.system.authoring.service.entity.Grade;
import com.brunosong.deployment.system.authoring.service.entity.Semester;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import org.springframework.stereotype.Component;

import java.util.List;

/** 뼈대 초안 ↔ skeleton_drafts 문서. 어댑터는 무엇을 읽고 쓸지만 보고, 모양 바꾸기는 여기서 한다 */
@Component
public class SkeletonDraftDataAccessMapper {

    public SkeletonDraftEntity toEntity(SkeletonDraft draft) {
        return new SkeletonDraftEntity(draft.getCurriculumId(), draft.getTitle(), draft.getDescription(),
                draft.getSortOrder(), toGradeEntities(draft.getGrades()), draft.getRevision());
    }

    /** 조건부 갱신에서 grades 칸만 통째로 바꿀 때 쓴다 */
    public List<GradeEntity> toGradeEntities(List<Grade> grades) {
        return grades.stream()
                .map(g -> new GradeEntity(g.getId(), g.getTitle(), g.getDescription(),
                        g.getSemesters().stream()
                                .map(s -> new SemesterEntity(s.getId(), s.getTitle(), s.getDescription()))
                                .toList()))
                .toList();
    }

    /** 문서를 도메인 객체로 되돌린다. 저장된 revision 을 그대로 쓴다 */
    public SkeletonDraft toDomain(SkeletonDraftEntity e) {
        // 정렬 순서 칸이 생기기 전에 만든 문서는 0 으로 읽는다(맨 앞)
        return SkeletonDraft.restore(e.getCurriculumId(), e.getTitle(), e.getDescription(),
                e.getSortOrder() == null ? 0 : e.getSortOrder(), toGrades(e.getGrades()), e.getRevision());
    }

    private List<Grade> toGrades(List<GradeEntity> entities) {
        // 학년 칸이 생기기 전에 만든 문서에는 grades 가 없어서 null 로 온다
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(e -> Grade.restore(e.id(), e.title(), e.description(), toSemesters(e.semesters())))
                .toList();
    }

    private List<Semester> toSemesters(List<SemesterEntity> entities) {
        // 학기 칸이 생기기 전에 넣은 학년에는 semesters 가 없어서 null 로 온다
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(e -> Semester.restore(e.id(), e.title(), e.description()))
                .toList();
    }
}
