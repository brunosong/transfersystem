package com.brunosong.deployment.system.authoring.service.entity;

import com.brunosong.deployment.system.authoring.service.exception.GradeNotFoundException;
import com.brunosong.deployment.system.authoring.service.valueobject.Grade;
import com.brunosong.deployment.system.authoring.service.valueobject.Semester;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


/**
 * 커리큘럼의 뼈대 초안. 편집은 여기에 하고, 배포는 이걸 읽지 않는다(ADR 0008).
 * 커리큘럼 정보와 학년, 학기를 담는다. 과목은 따로 저장한다(과목 초안).
 */
@Getter
public class SkeletonDraft {

    // 새 초안의 revision. 고칠 때마다 1씩 오른다(낙관적 락)
    private static final int FIRST_REVISION = 1;

    private final String curriculumId;
    private final String title;
    private final String description;
    private final List<Grade> grades;
    private final int revision;

    private SkeletonDraft(String curriculumId, String title, String description, List<Grade> grades, int revision) {
        this.curriculumId = curriculumId;
        this.title = title;
        this.description = description;
        this.grades = List.copyOf(grades);   // 밖에서 목록을 고쳐도 초안은 바뀌지 않게 복사해 둔다
        this.revision = revision;
    }

    /** 새 커리큘럼을 만든다. ID(CUR001 같은 번호)는 번호표에서 받아 온다 */
    public static SkeletonDraft create(String curriculumId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("커리큘럼 제목이 비어 있다.");
        }
        return new SkeletonDraft(curriculumId, title, description, List.of(), FIRST_REVISION);
    }


    /** 저장소에서 읽은 값으로 초안을 다시 만든다. revision 을 그대로 받는다 */
    public static SkeletonDraft restore(String curriculumId, String title, String description,
                                        List<Grade> grades, int revision) {
        return new SkeletonDraft(curriculumId, title, description, grades, revision);
    }

    /**
     * 제목과 설명을 고친 새 초안을 돌려준다. revision은 1 오른다.
     * 자기 자신은 바꾸지 않는다. 저장이 실패하면 (누가 먼저 고쳤으면) 고친 것을 그냥 버리면 된다.
     */
    public SkeletonDraft edit(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("커리큘럼 제목이 비어 있다.");
        }
        return new SkeletonDraft(curriculumId, title, description, grades, revision + 1);
    }

    /** 학년을 맨 뒤에 붙인 새 초안. revision 은 1 오른다. */
    public SkeletonDraft addGrade(Grade grade) {
        List<Grade> added = new ArrayList<>(grades);
        added.add(grade);
        return new SkeletonDraft(curriculumId, title, description, added, revision + 1);
    }

    /** 그 학년에 학기를 붙인 새 초안. revision 은 1 오른다. 학년이 없으면 GradeNotFoundException */
    public SkeletonDraft addSemester(String gradeId, Semester semester) {
        boolean found = grades.stream().anyMatch(g -> g.id().equals(gradeId));
        if (!found) {
            throw new GradeNotFoundException(gradeId);
        }
        // 그 학년만 학기를 붙인 것으로 바꾸고, 나머지 학년은 그대로 둔다
        List<Grade> changed = grades.stream()
                .map(g -> g.id().equals(gradeId) ? g.addSemester(semester) : g)
                .toList();
        return new SkeletonDraft(curriculumId, title, description, changed, revision + 1);
    }

}
