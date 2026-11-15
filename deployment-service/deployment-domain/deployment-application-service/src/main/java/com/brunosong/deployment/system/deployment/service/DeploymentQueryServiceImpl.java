package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.deployment.system.deployment.service.dto.query.TargetView;
import com.brunosong.deployment.system.deployment.service.helper.deployment.TargetCatalog;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentDetailView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentProgressView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSearchCondition;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSummaryView;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.deployment.system.deployment.service.ports.input.service.DeploymentQueryService;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeploymentQueryServiceImpl implements DeploymentQueryService {

    private final DeploymentQueryRepository deploymentQueryRepository;

    @Override
    public PageView<DeploymentSummaryView> findDeployments(DeploymentSearchCondition condition) {
        return deploymentQueryRepository.findDeployments(condition).map(record -> new DeploymentSummaryView(
                record.deploymentId(),
                record.sourceId(),
                record.sourceType(),
                record.status().name(),
                progressOf(record),
                record.createdBy(),
                record.createdAt(),
                record.updatedAt()));
    }

    @Override
    public DeploymentDetailView findDeployment(UUID deploymentId) {
        DeploymentRecord record = deploymentQueryRepository.findDeployment(deploymentId)
                .orElseThrow(() -> new DeploymentNotFoundException("Deployment not found: " + deploymentId));
        List<DeploymentDetailView.ChunkView> chunks = deploymentQueryRepository.findChunks(deploymentId).stream()
                .map(DeploymentDetailView.ChunkView::from)
                .toList();

        return new DeploymentDetailView(
                record.deploymentId(),
                record.sourceId(),
                record.sourceType(),
                record.sourceConfigId(),
                null,
                record.targetId(),
                record.status().name(),
                record.resultMessage(),
                progressOf(record),
                record.createdBy(),
                record.createdAt(),
                record.updatedAt(),
                chunks);
    }

    @Override
    public List<SourceConfigView> findSourceConfigs() {
        return deploymentQueryRepository.findSourceConfigs();
    }

    @Override
    public List<TargetView> findTargets() {
        return TargetCatalog.all().entrySet().stream()
                .map(entry -> new TargetView(entry.getKey(), entry.getValue().system(),
                        entry.getValue().environment(), true))
                .toList();
    }

    @Override
    public Map<String, Long> countByStatus() {
        Map<DeploymentStatus, Long> counts = deploymentQueryRepository.countByStatus();
        Map<String, Long> result = new LinkedHashMap<>();
        for (DeploymentStatus status : DeploymentStatus.values()) {
            result.put(status.name(), counts.getOrDefault(status, 0L));
        }
        return result;
    }

    static DeploymentProgressView progressOf(DeploymentRecord record) {
        return new DeploymentProgressView(record.totalChunks(), record.succeededChunks(), record.failedChunks());
    }
}
