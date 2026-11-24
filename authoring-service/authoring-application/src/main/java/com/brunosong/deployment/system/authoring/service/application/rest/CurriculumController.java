package com.brunosong.deployment.system.authoring.service.application.rest;

import com.brunosong.deployment.system.authoring.service.ports.input.service.CurriculumCreateService;
import com.brunosong.deployment.system.authoring.service.ports.input.service.CurriculumEditService;
import com.brunosong.deployment.system.authoring.service.ports.input.service.CurriculumQueryService;
import com.brunosong.deployment.system.authoring.service.ports.input.service.SubjectCreateService;
import com.brunosong.deployment.system.authoring.service.ports.input.service.SubjectEditService;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftPage;
import com.brunosong.deployment.system.authoring.service.entity.Grade;
import com.brunosong.deployment.system.authoring.service.entity.MoveDirection;
import com.brunosong.deployment.system.authoring.service.entity.Semester;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/curricula")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumCreateService curriculumCreateService;
    private final CurriculumQueryService curriculumQueryService;
    private final CurriculumEditService curriculumEditService;
    private final SubjectCreateService subjectCreateService;
    private final SubjectEditService subjectEditService;

    /**
     * 커리큘럼을 만든다. 번호를 받아 빈 뼈대 초안이 생긴다.
     * 제목이 비면 여기서 400 으로 막는다. 도메인도 같은 검사를 하지만, 그건 다른 길로 들어올 때를 위한 것이다
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCurriculumResponse create(@Valid @RequestBody CreateCurriculumRequest request) {
        SkeletonDraft draft = curriculumCreateService.create(request.title(), request.description());
        return new CreateCurriculumResponse(draft.getCurriculumId(), draft.getRevision());
    }

    /** 커리큘럼 목록. q 는 ID나 제목의 일부(비면 전부), page 는 0부터. ID 순이다 */
    @GetMapping
    public CurriculumListResponse findPage(@RequestParam(defaultValue = "") String q,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        SkeletonDraftPage found = curriculumQueryService.findPage(q, page, size);
        List<CurriculumListItem> items = found.items().stream()
                .map(d -> new CurriculumListItem(d.getCurriculumId(), d.getTitle(), d.getSortOrder(), d.getGrades().size(),
                        d.getRevision()))
                .toList();
        return new CurriculumListResponse(items, found.total());
    }

    /** 커리큘럼 하나. 편집 화면은 여기서 받은 revision 을 들고 있다가 고칠 때 함께 보낸다. 없으면 404 */
    @GetMapping("/{curriculumId}")
    public CurriculumResponse find(@PathVariable String curriculumId) {
        return CurriculumResponse.from(curriculumQueryService.find(curriculumId));
    }

    /**
     * 커리큘럼 제목과 설명을 고친다. revision 은 편집 화면을 열 때 받아 둔 값이다.
     * 그사이 누가 먼저 고쳤으면 409, 없으면 404. 성공하면 새 revision 을 돌려준다
     */
    @PutMapping("/{curriculumId}")
    public CurriculumResponse edit(@PathVariable String curriculumId, @Valid @RequestBody EditCurriculumRequest request) {
        return CurriculumResponse.from(
                curriculumEditService.edit(curriculumId, request.title(), request.description(), request.sortOrder(),
                        request.revision()));
    }

    /**
     * 학년을 하나 붙인다. 뼈대 초안을 고치는 일이라 revision 이 필요하다.
     * 그사이 누가 먼저 고쳤으면 409, 없으면 404. 새 학년의 ID와 새 revision 을 돌려준다
     */
    @PostMapping("/{curriculumId}/grades")
    @ResponseStatus(HttpStatus.CREATED)
    public AddGradeResponse addGrade(@PathVariable String curriculumId, @Valid @RequestBody AddGradeRequest request) {
        CurriculumEditService.AddedGrade added =
                curriculumEditService.addGrade(curriculumId, request.title(), request.description(), request.revision());
        return new AddGradeResponse(added.grade().getId(), added.revision());
    }

    /**
     * 학년의 제목과 설명을 고친다. 학년도 뼈대 초안 안에 있어서 revision 이 필요하다.
     * 그사이 누가 먼저 고쳤으면 409, 커리큘럼이나 학년이 없으면 404. 고친 커리큘럼 전체를 돌려준다
     */
    @PutMapping("/{curriculumId}/grades/{gradeId}")
    public CurriculumResponse editGrade(@PathVariable String curriculumId, @PathVariable String gradeId,
                                        @Valid @RequestBody EditGradeRequest request) {
        return CurriculumResponse.from(curriculumEditService.editGrade(
                curriculumId, gradeId, request.title(), request.description(), request.revision()));
    }

    /**
     * 학년을 지운다. 그 안의 학기도 함께 지워진다.
     * revision 은 쿼리로 받는다. DELETE 요청의 본문은 의미가 정해져 있지 않아 버려지기도 한다.
     * 그사이 누가 먼저 고쳤으면 409, 커리큘럼이나 학년이 없으면 404. 고친 커리큘럼 전체를 돌려준다
     */
    @DeleteMapping("/{curriculumId}/grades/{gradeId}")
    public CurriculumResponse removeGrade(@PathVariable String curriculumId, @PathVariable String gradeId,
                                          @RequestParam int revision) {
        return CurriculumResponse.from(curriculumEditService.removeGrade(curriculumId, gradeId, revision));
    }

    /**
     * 학기의 제목과 설명을 고친다. 학기도 뼈대 초안 안에 있어서 revision 이 필요하다.
     * 그사이 누가 먼저 고쳤으면 409, 커리큘럼·학년·학기가 없으면 404. 고친 커리큘럼 전체를 돌려준다
     */
    @PutMapping("/{curriculumId}/grades/{gradeId}/semesters/{semesterId}")
    public CurriculumResponse editSemester(@PathVariable String curriculumId, @PathVariable String gradeId,
                                           @PathVariable String semesterId, @Valid @RequestBody EditSemesterRequest request) {
        return CurriculumResponse.from(curriculumEditService.editSemester(
                curriculumId, gradeId, semesterId, request.title(), request.description(), request.revision()));
    }

    /**
     * 학년 하나에 학기를 붙인다. 학기도 뼈대 초안 안에 있어서 revision 이 필요하다.
     * 그사이 누가 먼저 고쳤으면 409, 커리큘럼이나 학년이 없으면 404
     */
    @PostMapping("/{curriculumId}/grades/{gradeId}/semesters")
    @ResponseStatus(HttpStatus.CREATED)
    public AddSemesterResponse addSemester(@PathVariable String curriculumId, @PathVariable String gradeId,
                                           @Valid @RequestBody AddSemesterRequest request) {
        CurriculumEditService.AddedSemester added = curriculumEditService.addSemester(
                curriculumId, gradeId, request.title(), request.description(), request.revision());
        return new AddSemesterResponse(added.semester().getId(), added.revision());
    }

    // ─── 과목 초안. 뼈대와 다른 애그리거트라 뼈대의 revision 을 받지 않는다 ───

    /** 한 커리큘럼의 과목 목록. 과목마다 자기 revision 이 있다 */
    @GetMapping("/{curriculumId}/subjects")
    public List<SubjectResponse> findSubjects(@PathVariable String curriculumId) {
        return curriculumQueryService.findSubjects(curriculumId).stream().map(SubjectResponse::from).toList();
    }

    /**
     * 과목을 만든다. 학기가 뼈대에 있어야 한다(없으면 404). 뼈대는 고치지 않으므로 뼈대 revision 은 필요 없다.
     * 새 과목의 ID와 과목 자신의 revision(1)을 돌려준다
     */
    @PostMapping("/{curriculumId}/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse createSubject(@PathVariable String curriculumId, @Valid @RequestBody CreateSubjectRequest request) {
        return SubjectResponse.from(
                subjectCreateService.create(curriculumId, request.semesterId(), request.title(), request.description()));
    }

    /**
     * 과목에 단원을 하나 붙인다. revision 은 그 과목의 revision 이다(뼈대 revision 이 아니다).
     * 그사이 누가 먼저 그 과목을 고쳤으면 409, 커리큘럼에 그 과목이 없으면 404
     */
    @PostMapping("/{curriculumId}/subjects/{subjectId}/units")
    @ResponseStatus(HttpStatus.CREATED)
    public AddUnitResponse addUnit(@PathVariable String curriculumId, @PathVariable String subjectId,
                                   @Valid @RequestBody AddUnitRequest request) {
        SubjectEditService.AddedUnit added = subjectEditService.addUnit(
                curriculumId, subjectId, request.title(), request.description(), request.revision());
        return new AddUnitResponse(added.unit().getId(), added.revision());
    }

    /**
     * 과목 제목과 설명을 고친다. revision 은 그 과목의 revision 이다.
     * 그사이 누가 먼저 그 과목을 고쳤으면 409, 커리큘럼에 그 과목이 없으면 404. 고친 과목을 돌려준다
     */
    @PutMapping("/{curriculumId}/subjects/{subjectId}")
    public SubjectResponse editSubject(@PathVariable String curriculumId, @PathVariable String subjectId,
                                       @Valid @RequestBody EditSubjectRequest request) {
        return SubjectResponse.from(subjectEditService.editSubject(
                curriculumId, subjectId, request.title(), request.description(), request.sortOrder(), request.revision()));
    }

    public record EditSubjectRequest(@NotBlank String title, String description, @NotNull Integer sortOrder,
                                     @NotNull Integer revision) {
    }

    /**
     * 단원에 차시를 하나 붙인다. revision 은 그 과목의 revision 이다.
     * 그사이 누가 먼저 그 과목을 고쳤으면 409, 과목이나 단원이 없으면 404
     */
    @PostMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public AddLessonResponse addLesson(@PathVariable String curriculumId, @PathVariable String subjectId,
                                       @PathVariable String unitId, @Valid @RequestBody AddLessonRequest request) {
        SubjectEditService.AddedLesson added = subjectEditService.addLesson(
                curriculumId, subjectId, unitId, request.title(), request.description(), request.revision());
        return new AddLessonResponse(added.lesson().getId(), added.revision());
    }

    /**
     * 단원의 제목과 설명을 고친다. revision 은 그 과목의 revision 이다.
     * 그사이 누가 먼저 그 과목을 고쳤으면 409, 과목이나 단원이 없으면 404. 고친 과목을 돌려준다
     */
    @PutMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}")
    public SubjectResponse editUnit(@PathVariable String curriculumId, @PathVariable String subjectId,
                                    @PathVariable String unitId, @Valid @RequestBody EditUnitRequest request) {
        return SubjectResponse.from(subjectEditService.editUnit(
                curriculumId, subjectId, unitId, request.title(), request.description(), request.revision()));
    }

    /**
     * 차시의 제목과 설명을 고친다. revision 은 그 과목의 revision 이다.
     * 그사이 누가 먼저 그 과목을 고쳤으면 409, 과목·단원·차시가 없으면 404. 고친 과목을 돌려준다
     */
    @PutMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}/lessons/{lessonId}")
    public SubjectResponse editLesson(@PathVariable String curriculumId, @PathVariable String subjectId,
                                      @PathVariable String unitId, @PathVariable String lessonId,
                                      @Valid @RequestBody EditLessonRequest request) {
        return SubjectResponse.from(subjectEditService.editLesson(
                curriculumId, subjectId, unitId, lessonId, request.title(), request.description(), request.learningLevel(),
                request.revision()));
    }

    // ─── 순서 옮기기. 목록 안의 자리가 순서인 단계(학년, 학기, 단원, 차시)를 한 칸 옮긴다 ───

    @PostMapping("/{curriculumId}/grades/{gradeId}/move")
    public CurriculumResponse moveGrade(@PathVariable String curriculumId, @PathVariable String gradeId,
                                        @Valid @RequestBody MoveRequest request) {
        return CurriculumResponse.from(
                curriculumEditService.moveGrade(curriculumId, gradeId, request.direction(), request.revision()));
    }

    @PostMapping("/{curriculumId}/grades/{gradeId}/semesters/{semesterId}/move")
    public CurriculumResponse moveSemester(@PathVariable String curriculumId, @PathVariable String gradeId,
                                           @PathVariable String semesterId, @Valid @RequestBody MoveRequest request) {
        return CurriculumResponse.from(curriculumEditService.moveSemester(
                curriculumId, gradeId, semesterId, request.direction(), request.revision()));
    }

    /** revision 은 그 과목의 revision 이다 */
    @PostMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}/move")
    public SubjectResponse moveUnit(@PathVariable String curriculumId, @PathVariable String subjectId,
                                    @PathVariable String unitId, @Valid @RequestBody MoveRequest request) {
        return SubjectResponse.from(subjectEditService.moveUnit(
                curriculumId, subjectId, unitId, request.direction(), request.revision()));
    }

    /** revision 은 그 과목의 revision 이다 */
    @PostMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}/lessons/{lessonId}/move")
    public SubjectResponse moveLesson(@PathVariable String curriculumId, @PathVariable String subjectId,
                                      @PathVariable String unitId, @PathVariable String lessonId,
                                      @Valid @RequestBody MoveRequest request) {
        return SubjectResponse.from(subjectEditService.moveLesson(
                curriculumId, subjectId, unitId, lessonId, request.direction(), request.revision()));
    }

    /** direction 은 UP 또는 DOWN */
    public record MoveRequest(@NotNull MoveDirection direction, @NotNull Integer revision) {
    }

    // ─── 지우기. revision 은 쿼리로 받는다(DELETE 의 본문은 버려지기도 한다) ───

    /** 학기를 지운다. 과목이 남아 있으면 422, 누가 먼저 고쳤으면 409, 없으면 404 */
    @DeleteMapping("/{curriculumId}/grades/{gradeId}/semesters/{semesterId}")
    public CurriculumResponse removeSemester(@PathVariable String curriculumId, @PathVariable String gradeId,
                                             @PathVariable String semesterId, @RequestParam int revision) {
        return CurriculumResponse.from(curriculumEditService.removeSemester(curriculumId, gradeId, semesterId, revision));
    }

    /** 과목을 지운다. 단원과 차시도 함께 사라진다. revision 은 그 과목의 revision 이다 */
    @DeleteMapping("/{curriculumId}/subjects/{subjectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSubject(@PathVariable String curriculumId, @PathVariable String subjectId,
                              @RequestParam int revision) {
        subjectEditService.removeSubject(curriculumId, subjectId, revision);
    }

    /** 단원을 지운다. 그 안의 차시도 함께 사라진다 */
    @DeleteMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}")
    public SubjectResponse removeUnit(@PathVariable String curriculumId, @PathVariable String subjectId,
                                      @PathVariable String unitId, @RequestParam int revision) {
        return SubjectResponse.from(subjectEditService.removeUnit(curriculumId, subjectId, unitId, revision));
    }

    /** 차시를 지운다 */
    @DeleteMapping("/{curriculumId}/subjects/{subjectId}/units/{unitId}/lessons/{lessonId}")
    public SubjectResponse removeLesson(@PathVariable String curriculumId, @PathVariable String subjectId,
                                        @PathVariable String unitId, @PathVariable String lessonId,
                                        @RequestParam int revision) {
        return SubjectResponse.from(subjectEditService.removeLesson(curriculumId, subjectId, unitId, lessonId, revision));
    }

    public record EditLessonRequest(@NotBlank String title, String description,
                                    @NotNull @Min(1) @Max(5) Integer learningLevel, @NotNull Integer revision) {
    }

    public record EditUnitRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddLessonRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddLessonResponse(String lessonId, int revision) {
    }

    public record AddUnitRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddUnitResponse(String unitId, int revision) {
    }

    public record CreateSubjectRequest(@NotBlank String semesterId, @NotBlank String title, String description) {
    }

    public record SubjectResponse(String id, String semesterId, String title, String description, int sortOrder,
                                  List<UnitResponse> units, int revision) {

        static SubjectResponse from(SubjectDraft subject) {
            List<UnitResponse> units = subject.getUnits().stream()
                    .map(u -> new UnitResponse(u.getId(), u.getTitle(), u.getDescription(),
                            u.getLessons().stream()
                                    .map(l -> new LessonResponse(l.getId(), l.getTitle(), l.getDescription(), l.getLearningLevel()))
                                    .toList()))
                    .toList();
            return new SubjectResponse(subject.getId(), subject.getSemesterId(), subject.getTitle(),
                    subject.getDescription(), subject.getSortOrder(), units, subject.getRevision());
        }
    }

    public record UnitResponse(String id, String title, String description, List<LessonResponse> lessons) {
    }

    public record LessonResponse(String id, String title, String description, int learningLevel) {
    }

    public record CurriculumListResponse(List<CurriculumListItem> items, long total) {
    }

    public record CurriculumListItem(String curriculumId, String title, int sortOrder, int gradeCount, int revision) {
    }

    public record CreateCurriculumRequest(@NotBlank String title, String description) {
    }

    // revision 을 빠뜨리면 0 으로 들어가 늘 409 가 되므로, 빠뜨린 것을 400 으로 알려 준다
    public record EditCurriculumRequest(@NotBlank String title, String description, @NotNull Integer sortOrder,
                                        @NotNull Integer revision) {
    }

    public record AddGradeRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record EditSemesterRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddGradeResponse(String gradeId, int revision) {
    }

    public record EditGradeRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddSemesterRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddSemesterResponse(String semesterId, int revision) {
    }

    public record CreateCurriculumResponse(String curriculumId, int revision) {
    }

    public record CurriculumResponse(String curriculumId, String title, String description, int sortOrder,
                                     List<GradeResponse> grades, int revision) {

        static CurriculumResponse from(SkeletonDraft draft) {
            List<GradeResponse> grades = draft.getGrades().stream().map(GradeResponse::from).toList();
            return new CurriculumResponse(draft.getCurriculumId(), draft.getTitle(), draft.getDescription(),
                    draft.getSortOrder(), grades, draft.getRevision());
        }
    }

    public record GradeResponse(String id, String title, String description, List<SemesterResponse> semesters) {

        static GradeResponse from(Grade grade) {
            List<SemesterResponse> semesters = grade.getSemesters().stream().map(SemesterResponse::from).toList();
            return new GradeResponse(grade.getId(), grade.getTitle(), grade.getDescription(), semesters);
        }
    }

    public record SemesterResponse(String id, String title, String description) {

        static SemesterResponse from(Semester semester) {
            return new SemesterResponse(semester.getId(), semester.getTitle(), semester.getDescription());
        }
    }
}
