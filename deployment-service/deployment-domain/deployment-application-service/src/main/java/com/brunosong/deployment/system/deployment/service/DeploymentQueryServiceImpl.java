package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentDetailView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentProgressView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSearchCondition;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentStepView;
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

    static final String ACTION_APPLY = "APPLY";

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
        List<ChunkRecord> chunks = deploymentQueryRepository.findChunks(deploymentId);
        DeploymentProgressView progress = progressOf(record);

        // 사가 이전이라 단계는 "대상 하나에 반영" 하나뿐이다. 사가가 들어오면
        // 사가 상태 테이블에서 단계 목록을 읽어 그대로 내려 주면 된다.
        DeploymentStepView applyStep = new DeploymentStepView(
                1,
                ACTION_APPLY,
                record.targetId(),
                null,
                stepStatus(record.status(), progress),
                progress,
                chunks);

        return new DeploymentDetailView(
                record.deploymentId(),
                record.sourceId(),
                record.sourceType(),
                record.sourceConfigId(),
                record.status().name(),
                record.resultMessage(),
                progress,
                record.createdBy(),
                record.createdAt(),
                record.updatedAt(),
                List.of(applyStep));
    }

    @Override
    public List<SourceConfigView> findSourceConfigs() {
        return deploymentQueryRepository.findSourceConfigs();
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

    /**
     * 단계 상태는 청크 결과를 먼저 본다. 실패한 청크가 하나라도 있으면 실패, 모든 청크가
     * 성공했으면 성공, 아직 아무 결과도 없고 배포 전이면 대기, 그 밖에는 진행 중이다.
     */
    static String stepStatus(DeploymentStatus deploymentStatus, DeploymentProgressView progress) {
        if (progress.failed() > 0 || deploymentStatus == DeploymentStatus.FAILED) {
            return "FAILED";
        }
        if (deploymentStatus == DeploymentStatus.SUCCESS
                || (progress.total() > 0 && progress.succeeded() >= progress.total())) {
            return "SUCCEEDED";
        }
        if (deploymentStatus == DeploymentStatus.PENDING && progress.succeeded() == 0) {
            return "PENDING";
        }
        return "PROCESSING";
    }
}
