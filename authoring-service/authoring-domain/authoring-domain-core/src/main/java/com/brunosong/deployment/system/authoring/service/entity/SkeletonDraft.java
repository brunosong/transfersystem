package com.brunosong.deployment.system.authoring.service.entity;

import com.brunosong.deployment.system.authoring.service.exception.GradeNotFoundException;
import com.brunosong.deployment.system.authoring.service.exception.SemesterNotFoundException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 커리큘럼의 뼈대 초안(애그리거트 루트). 커리큘럼 정보와 학년, 학기를 담는다. 과목은 따로 저장한다(과목 초안).
 * 편집은 여기에 하고, 배포는 이걸 읽지 않는다(ADR 0008).
 * 고칠 때마다 revision 이 1 오른다. 저장할 때 화면이 본 revision 과 비교한다(낙관적 락)
 */
@Getter
public class SkeletonDraft {

    private static final int FIRST_REVISION = 1;

    private final String curriculumId;
    private String title;
    private String description;
    private final List<Grade> grades;
    private int revision;

    private SkeletonDraft(String curriculumId, String title, String description, List<Grade> grades, int revision) {
        this.curriculumId = curriculumId;
        this.title = title;
        this.description = description;
        this.grades = new ArrayList<>(grades);   // 안에서 고칠 수 있게 우리 목록으로 복사한다
        this.revision = revision;
    }

    /** 새 커리큘럼을 만든다. ID(CUR001 같은 번호)는 번호표에서 받아 온다 */
    public static SkeletonDraft create(String curriculumId, String title, String description) {
        checkTitle(title);
        return new SkeletonDraft(curriculumId, title, description, List.of(), FIRST_REVISION);
    }

    /** 저장소에서 읽은 값으로 초안을 다시 만든다. revision 을 그대로 받는다 */
    public static SkeletonDraft restore(String curriculumId, String title, String description,
                                        List<Grade> grades, int revision) {
        return new SkeletonDraft(curriculumId, title, description, grades, revision);
    }

    /** 밖에서는 읽기만 한다. 고치려면 아래 메서드를 쓴다 */
    public List<Grade> getGrades() {
        return Collections.unmodifiableList(grades);
    }

    /** 커리큘럼 제목과 설명을 고친다. revision 이 1 오른다 */
    public void edit(String title, String description) {
        checkTitle(title);
        this.title = title;
        this.description = description;
        revision++;
    }

    /** 학년을 맨 뒤에 붙인다. revision 이 1 오른다 */
    public void addGrade(Grade grade) {
        grades.add(grade);
        revision++;
    }

    /** 학년에 학기를 붙인다. revision 이 1 오른다. 학년이 없으면 GradeNotFoundException */
    public void addSemester(String gradeId, Semester semester) {
        findGrade(gradeId).addSemester(semester);
        revision++;
    }

    /** 학년의 제목과 설명을 고친다. revision 이 1 오른다. 학년이 없으면 GradeNotFoundException */
    public void editGrade(String gradeId, String title, String description) {
        findGrade(gradeId).edit(title, description);
        revision++;
    }

    /** 학기의 제목과 설명을 고친다. revision 이 1 오른다. 학년이나 학기가 없으면 각각의 NotFoundException */
    public void editSemester(String gradeId, String semesterId, String title, String description) {
        findSemester(gradeId, semesterId).edit(title, description);
        revision++;
    }

    private Grade findGrade(String gradeId) {
        return grades.stream()
                .filter(g -> g.getId().equals(gradeId))
                .findFirst()
                .orElseThrow(() -> new GradeNotFoundException(gradeId));
    }

    private Semester findSemester(String gradeId, String semesterId) {
        return findGrade(gradeId).getSemesters().stream()
                .filter(s -> s.getId().equals(semesterId))
                .findFirst()
                .orElseThrow(() -> new SemesterNotFoundException(gradeId, semesterId));
    }

    private static void checkTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("커리큘럼 제목이 비어 있다");
        }
    }

    /** 학년을 지운다. 그 안의 학기도 함께 사라진다. revision 이 1 오른다. 학년이 없으면 GradeNotFoundException */
    public void removeGrade(String gradeId) {
        grades.remove(findGrade(gradeId));
        revision++;
    }

    /** 이 뼈대에 그 학기가 있는가. 과목을 만들 때 유스케이스가 묻는다. (과목은 뼈대 안을 모른다) */
    public boolean hasSemester(String semesterId) {
        return grades.stream()
                .flatMap(g -> g.getSemesters().stream())
                .anyMatch(s -> s.getId().equals(semesterId));
    }


}
