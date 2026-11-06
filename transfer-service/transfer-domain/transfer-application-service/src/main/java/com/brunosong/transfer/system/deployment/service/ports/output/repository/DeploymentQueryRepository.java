package com.brunosong.transfer.system.deployment.service.ports.output.repository;

import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import com.brunosong.transfer.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.transfer.system.deployment.service.dto.query.PageView;
import com.brunosong.transfer.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentSearchCondition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 조회 전용 출력 포트. 도메인 애그리거트를 거치지 않고 읽기 모델을 바로 돌려준다.
 */
public interface DeploymentQueryRepository {

    PageView<DeploymentRecord> findDeployments(DeploymentSearchCondition condition);

    Optional<DeploymentRecord> findDeployment(UUID deploymentId);

    List<ChunkRecord> findChunks(UUID deploymentId);

    List<SourceConfigView> findSourceConfigs();

    Map<DeploymentStatus, Long> countByStatus();
}
