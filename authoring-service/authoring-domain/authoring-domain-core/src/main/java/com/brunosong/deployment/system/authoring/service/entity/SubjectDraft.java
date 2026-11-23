package com.brunosong.deployment.system.authoring.service.entity;

import com.brunosong.deployment.system.authoring.service.exception.UnitNotFoundException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * 과목 초안(애그리거트 루트). 과목 하나와 그 아래 단원, 차시를 담는다. 차시는 다음 기능에서 더한다.
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
    private final List<Unit> units;

    private SubjectDraft(String id, String curriculumId, String semesterId, String title, String description,
                         int revision, List<Unit> units) {
        this.id = id;
        this.curriculumId = curriculumId;
        this.semesterId = semesterId;
        this.title = title;
        this.description = description;
        this.revision = revision;
        this.units = new ArrayList<>(units);
    }

    /**
     * 새 과목. ID는 시스템이 만든다.
     * 학기가 뼈대에 있는지는 여기서 보지 않는다. 과목은 뼈대 안을 모르기 때문이다(유스케이스가 본다)
     */
    public static SubjectDraft create(String curriculumId, String semesterId, String title, String description) {
        checkTitle(title);
        return new SubjectDraft("SUBJECT-" + UUID.randomUUID(), curriculumId, semesterId, title, description, FIRST_REVISION, List.of());
    }

    /** 저장소에서 읽은 값으로 다시 만든다. revision 을 그대로 받는다. */
    public static SubjectDraft restore(String id, String curriculumId, String semesterId, String title, String description, int revision, List<Unit> units) {
        return new SubjectDraft(id, curriculumId, semesterId, title, description, revision, units);
    }

    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    public void addUnit(Unit unit) {
        units.add(unit);
        revision++;
    }

    /** 과목 제목과 설명을 고친다. revision 이 1 오른다 */
    public void edit(String title, String description) {
        checkTitle(title);
        this.title = title;
        this.description = description;
        revision++;
    }

    private static void checkTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("과목 제목이 비어 있다");
        }
    }

    /** 단원에 차시를 붙인다. 과목의 revision 이 1 오른다. 단원이 없으면 UnitNotFoundException */
    public void addLesson(String unitId, Lesson lesson) {
        findUnit(unitId).addLesson(lesson);
        revision++;
    }

    private Unit findUnit(String unitId) {
        return units.stream()
                .filter(u -> u.getId().equals(unitId))
                .findFirst()
                .orElseThrow(() -> new UnitNotFoundException(id, unitId));
    }
}
