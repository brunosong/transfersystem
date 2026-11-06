package com.brunosong.transfer.system.deployment.service.application.rest;

import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import com.brunosong.transfer.system.deployment.service.dto.create.CreateDeploymentResponse;
import com.brunosong.transfer.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.transfer.system.deployment.service.dto.query.PageView;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentDetailView;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentSearchCondition;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentSummaryView;
import com.brunosong.transfer.system.deployment.service.ports.input.service.DeploymentApplicationService;
import com.brunosong.transfer.system.deployment.service.ports.input.service.DeploymentQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentApplicationService deploymentApplicationService;
    private final DeploymentQueryService deploymentQueryService;

    /**
     * 배포를 접수한다. 데이터를 청크로 나눠 아웃박스에 저장하고 바로 돌아오며,
     * 실제 반영은 비동기로 진행된다. 그래서 200 이 아니라 202 와 조회 위치를 돌려준다.
     */
    @PostMapping
    public ResponseEntity<CreateDeploymentResponse> createDeployment(@Valid @RequestBody DeploymentRequest deploymentRequest) {
        CreateDeploymentResponse response = deploymentApplicationService.executeDeployment(deploymentRequest);
        return ResponseEntity.accepted()
                .location(URI.create("/api/v1/deployments/" + response.deploymentId()))
                .body(response);
    }

    @GetMapping
    public PageView<DeploymentSummaryView> findDeployments(@RequestParam(required = false) DeploymentStatus status,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return deploymentQueryService.findDeployments(new DeploymentSearchCondition(status, page, size));
    }

    /** 상태별 배포 수. 목록 화면 상단의 집계 카드에 쓴다. */
    @GetMapping("/stats")
    public Map<String, Long> countByStatus() {
        return deploymentQueryService.countByStatus();
    }

    @GetMapping("/{deploymentId}")
    public DeploymentDetailView findDeployment(@PathVariable UUID deploymentId) {
        return deploymentQueryService.findDeployment(deploymentId);
    }
}
