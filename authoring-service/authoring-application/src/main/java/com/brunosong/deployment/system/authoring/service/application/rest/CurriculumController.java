package com.brunosong.deployment.system.authoring.service.application.rest;

import com.brunosong.deployment.system.authoring.service.CurriculumCreateService;
import com.brunosong.deployment.system.authoring.service.CurriculumEditService;
import com.brunosong.deployment.system.authoring.service.CurriculumQueryService;
import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    /** 커리큘럼 하나. 편집 화면은 여기서 받은 revision 을 들고 있다가 고칠 때 함께 보낸다. 없으면 404 */
    @GetMapping("/{curriculumId}")
    public CurriculumResponse find(@PathVariable String curriculumId) {
        SkeletonDraft draft = curriculumQueryService.find(curriculumId);
        return new CurriculumResponse(draft.getCurriculumId(), draft.getTitle(), draft.getDescription(), draft.getRevision());
    }

    /**
     * 커리큘럼 제목과 설명을 고친다. revision 은 편집 화면을 열 때 받아 둔 값이다.
     * 그사이 누가 먼저 고쳤으면 409, 없으면 404. 성공하면 새 revision 을 돌려준다
     */
    @PutMapping("/{curriculumId}")
    public CurriculumResponse edit(@PathVariable String curriculumId, @Valid @RequestBody EditCurriculumRequest request) {
        SkeletonDraft draft = curriculumEditService.edit(curriculumId, request.title(), request.description(), request.revision());
        return new CurriculumResponse(draft.getCurriculumId(), draft.getTitle(), draft.getDescription(), draft.getRevision());
    }

    public record CreateCurriculumRequest(@NotBlank String title, String description) {
    }

    // revision 을 빠뜨리면 0 으로 들어가 늘 409 가 되므로, 빠뜨린 것을 400 으로 알려 준다
    public record EditCurriculumRequest(@NotBlank String title, String description, @NotNull Integer revision) {
    }

    public record CreateCurriculumResponse(String curriculumId, int revision) {
    }

    public record CurriculumResponse(String curriculumId, String title, String description, int revision) {
    }
}
