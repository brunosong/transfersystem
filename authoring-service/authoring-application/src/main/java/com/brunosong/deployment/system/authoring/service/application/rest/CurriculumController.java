package com.brunosong.deployment.system.authoring.service.application.rest;

import com.brunosong.deployment.system.authoring.service.CurriculumCreateService;
import com.brunosong.deployment.system.authoring.service.CurriculumEditService;
import com.brunosong.deployment.system.authoring.service.CurriculumQueryService;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
import com.brunosong.deployment.system.authoring.service.ports.output.repository.SkeletonDraftPage;
import com.brunosong.deployment.system.authoring.service.valueobject.Grade;
import com.brunosong.deployment.system.authoring.service.valueobject.Semester;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .map(d -> new CurriculumListItem(d.getCurriculumId(), d.getTitle(), d.getGrades().size(), d.getRevision()))
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
                curriculumEditService.edit(curriculumId, request.title(), request.description(), request.revision()));
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
        return new AddGradeResponse(added.grade().id(), added.revision());
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
        return new AddSemesterResponse(added.semester().id(), added.revision());
    }

    public record CurriculumListResponse(List<CurriculumListItem> items, long total) {
    }

    public record CurriculumListItem(String curriculumId, String title, int gradeCount, int revision) {
    }

    public record CreateCurriculumRequest(@NotBlank String title, String description) {
    }

    // revision 을 빠뜨리면 0 으로 들어가 늘 409 가 되므로, 빠뜨린 것을 400 으로 알려 준다
    public record EditCurriculumRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddGradeRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddGradeResponse(String gradeId, int revision) {
    }

    public record AddSemesterRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record AddSemesterResponse(String semesterId, int revision) {
    }

    public record CreateCurriculumResponse(String curriculumId, int revision) {
    }

    public record CurriculumResponse(String curriculumId, String title, String description,
                                     List<GradeResponse> grades, int revision) {

        static CurriculumResponse from(SkeletonDraft draft) {
            List<GradeResponse> grades = draft.getGrades().stream().map(GradeResponse::from).toList();
            return new CurriculumResponse(draft.getCurriculumId(), draft.getTitle(), draft.getDescription(), grades, draft.getRevision());
        }
    }

    public record GradeResponse(String id, String title, String description, List<SemesterResponse> semesters) {

        static GradeResponse from(Grade grade) {
            List<SemesterResponse> semesters = grade.semesters().stream().map(SemesterResponse::from).toList();
            return new GradeResponse(grade.id(), grade.title(), grade.description(), semesters);
        }
    }

    public record SemesterResponse(String id, String title, String description) {

        static SemesterResponse from(Semester semester) {
            return new SemesterResponse(semester.id(), semester.title(), semester.description());
        }
    }
}
