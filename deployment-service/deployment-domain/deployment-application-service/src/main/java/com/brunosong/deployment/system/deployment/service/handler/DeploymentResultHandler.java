package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.entity.Chunk;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.outbox.scheduler.DeploymentOutboxHelper;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayload;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import com.brunosong.deployment.system.domain.DomainConstants;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeploymentResultHandler {

    private final DeploymentRepository deploymentRepository;
    private final ChunkPayloadRepository chunkPayloadRepository;
    private final DeploymentOutboxHelper deploymentOutboxHelper;
    private final DeploymentDataMapper deploymentDataMapper;

    /**
     * 대상이 청크 하나를 반영했다(또는 못 했다)는 결과를 배포에 기록한다.
     * 무엇을 이어서 보낼지는 도메인이 정한다. 뼈대가 반영되면 기다리던 과목들을 돌려받아 아웃박스에 넣는다(불변식 3).
     * 기록과 아웃박스 행을 한 트랜잭션에 묶어서, 상태만 바뀌고 과목이 안 나가는 일이 없게 한다(ADR 0004).
     */
    @Transactional
    public void applyResult(ApplyResultMessage result) {
        UUID deploymentId = result.getDeploymentId().getValue();
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new DeploymentNotFoundException("deploymentId is " + deploymentId));

        // 1. 도메인에 결과를 알린다. 성공이면 이제 보낼 청크를 돌려받는다(뼈대면 과목 전부, 과목이면 없음)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DomainConstants.UTC));
        List<Chunk> toSend = List.of();
        if (result.getDeploymentStatus() == DeploymentStatus.SUCCESS) {
            toSend = deployment.recordSuccess(result.getChunkOffset(), now.toInstant());
        } else {
            deployment.recordFailure(result.getChunkOffset(), result.getAttempt(), result.getMessage(), now.toInstant());
        }

        // 2. 배포와 청크 상태를 저장한다
        deploymentRepository.save(deployment);

        // 3. 도메인이 보내라고 한 청크를 내용 표에서 꺼내 아웃박스에 넣는다. 번호 순으로 넣으므로 seq 도 번호 순이다
        if (!toSend.isEmpty()) {
            List<Integer> chunkNos = toSend.stream().map(Chunk::getNo).toList();
            for (ChunkPayload payload : chunkPayloadRepository.findAll(deploymentId, chunkNos)) {
                deploymentOutboxHelper.saveDeploymentOutboxMessage(deploymentDataMapper.toDeploymentEventPayload(
                        deployment, payload.chunkNo(), payload.content(), now));
            }
        }
    }
}
