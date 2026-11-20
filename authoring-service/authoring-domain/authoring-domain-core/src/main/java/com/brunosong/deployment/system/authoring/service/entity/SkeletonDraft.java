package com.brunosong.deployment.system.authoring.service.entity;

import lombok.Getter;




/**
 * 커리큘럼의 뼈대 초안. 편집은 여기에 하고, 배포는 이걸 읽지 않는다(ADR 0008).
 * 지금은 커리큘럼 만들기에 필요한 칸만 있다. 학년과 학기는 다음 기능에서 더한다.
 */
@Getter
public class SkeletonDraft {

    // 새 초안의 revision. 고칠 때마다 1씩 오른다 (낙관적 락은 고치기 기능에서 붙인다)
    private static final int FIRST_REVISION = 1;

    private final String curriculumId;
    private final String title;
    private final String description;
    private final int revision;

    private SkeletonDraft(String curriculumId, String title, String description, int revision) {
        this.curriculumId = curriculumId;
        this.title = title;
        this.description = description;
        this.revision = revision;
    }

    /** 새 커리큘럼을 만든다. ID(CUR001 같은 번호)는 번호표에서 받아 온다 */
    public static SkeletonDraft create(String curriculumId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("커리큘럼 제목이 비어 있다.");
        }
        return new SkeletonDraft(curriculumId, title, description, FIRST_REVISION);
    }


}
