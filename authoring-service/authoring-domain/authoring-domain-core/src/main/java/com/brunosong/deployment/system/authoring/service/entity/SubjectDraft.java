package com.brunosong.deployment.system.authoring.service.entity;

import lombok.Getter;

import java.util.UUID;


/**
 * 과목 초안(애그리거트 루트). 과목 하나와 그 아래 단원, 차시를 담는다. 단원과 차시는 다음 기능에서 더한다.
 * 뼈대와 다른 애그리거트라 revision 을 따로 갖는다. 그래서 서로 다른 과목은 동시에 고쳐도 부딪히지 않는다.
 * 뼈대는 ID(curriculumId, semesterId)로만 가리킨다
 */
@Getter
public class SubjectDraft {

    private static final int FIRST_REVISION = 1;

    private final String id;
    private final String curriculumId;
    private String semesterId;
    private String title;
    private String description;
    private int revision;

    private SubjectDraft(String id, String curriculumId, String semesterId, String title, String description,
                         int revision) {
        this.id = id;
        this.curriculumId = curriculumId;
        this.semesterId = semesterId;
        this.title = title;
        this.description = description;
        this.revision = revision;
    }

    /**
     * 새 과목. ID는 시스템이 만든다.
     * 학기가 뼈대에 있는지는 여기서 보지 않는다. 과목은 뼈대 안을 모르기 때문이다(유스케이스가 본다)
     */
    public static SubjectDraft create(String curriculumId, String semesterId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("과목 제목이 비어 있다.");
        }
        return new SubjectDraft("SUBJECT-" + UUID.randomUUID(), curriculumId, semesterId, title, description, FIRST_REVISION);
    }

    /** 저장소에서 읽은 값으로 다시 만든다. revision 을 그대로 받는다. */
    public static SubjectDraft restore(String id, String curriculumId, String semesterId, String title, String description, int revision) {
        return new SubjectDraft(id, curriculumId, semesterId, title, description, revision);
    }
}
