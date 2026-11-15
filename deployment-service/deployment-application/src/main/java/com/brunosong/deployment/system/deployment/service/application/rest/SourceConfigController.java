package com.brunosong.deployment.system.deployment.service.application.rest;

import com.brunosong.deployment.system.deployment.service.dto.query.CurriculumSummaryView;
import com.brunosong.deployment.system.deployment.service.dto.query.CurriculumTreeView;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.deployment.system.deployment.service.ports.input.service.CurriculumQueryService;
import com.brunosong.deployment.system.deployment.service.ports.input.service.DeploymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/source-configs")
@RequiredArgsConstructor
public class SourceConfigController {

    private final DeploymentQueryService deploymentQueryService;
    private final CurriculumQueryService curriculumQueryService;

    @GetMapping
    public List<SourceConfigView> findSourceConfigs() {
        return deploymentQueryService.findSourceConfigs();
    }

    /** 원본 저장소의 커리큘럼 목록. q 는 커리큘럼 ID나 제목 */
    @GetMapping("/{sourceConfigId}/curriculums")
    public PageView<CurriculumSummaryView> findCurriculums(@PathVariable Long sourceConfigId,
                                                           @RequestParam(required = false) String q,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        return curriculumQueryService.findCurriculums(sourceConfigId, q, page, size);
    }

    /** 커리큘럼 하나의 트리. 배포 화면이 개수와 청크 수를 보여 줄 때 쓴다 */
    @GetMapping("/{sourceConfigId}/curriculums/{curriculumId}")
    public CurriculumTreeView findCurriculum(@PathVariable Long sourceConfigId, @PathVariable String curriculumId) {
        return curriculumQueryService.findCurriculum(sourceConfigId, curriculumId);
    }
}
