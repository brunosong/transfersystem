package com.brunosong.deployment.system.deployment.service.application.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/curriculums")
public class CurriculumController {

    /**
     * 커리큘럼 하나가 대상마다 마지막으로 어떻게 배포됐는지.
     * 아직 이 조회를 만들지 않아 늘 빈 목록을 돌려준다. 화면은 빈 목록이면 이력이 없다고 보고 접수를 막지 않는다.
     * 배포 표에 curriculum_id 가 생겼으므로(V10) 그 칸으로 대상별 마지막 배포를 모으면 된다.
     */
    @GetMapping("/{curriculumId}/deployments")
    public List<Object> findCurriculumDeployments(@PathVariable String curriculumId) {
        return List.of();
    }
}
