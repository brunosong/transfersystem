package com.brunosong.transfer.system.deployment.service.application.rest;

import com.brunosong.transfer.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.deployment.service.ports.input.service.DeploymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/source-configs")
@RequiredArgsConstructor
public class SourceConfigController {

    private final DeploymentQueryService deploymentQueryService;

    @GetMapping
    public List<SourceConfigView> findSourceConfigs() {
        return deploymentQueryService.findSourceConfigs();
    }
}
