package com.brunosong.deployment.system.deployment.service.application.rest;

import com.brunosong.deployment.system.deployment.service.dto.query.TargetView;
import com.brunosong.deployment.system.deployment.service.ports.input.service.DeploymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 배포할 대상 시스템. 대상 목록을 배포 쪽으로 옮기는 방향(ADR 0002)에 따라 배포 서비스가 답한다 */
@RestController
@RequestMapping("/api/v1/targets")
@RequiredArgsConstructor
public class TargetController {

    private final DeploymentQueryService deploymentQueryService;

    @GetMapping
    public List<TargetView> findTargets() {
        return deploymentQueryService.findTargets();
    }
}
