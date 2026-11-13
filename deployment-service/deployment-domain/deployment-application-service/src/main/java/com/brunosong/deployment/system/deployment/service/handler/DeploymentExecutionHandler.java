package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.entity.Chunk;
import com.brunosong.deployment.system.domain.DomainConstants;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.helper.deployment.DeploymentPersistHelper;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.scheduler.DeploymentOutboxHelper;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeploymentExecutionHandler {

    private final DeploymentPersistHelper deploymentPersistHelper;
    private final DeploymentOutboxHelper deploymentOutboxHelper;
    private final DeploymentDataMapper deploymentDataMapper;


    /**
     * 배포를 접수한다. 배포를 저장하고, 보낼 청크를 아웃박스에 적는다.
     * 여기서는 아무것도 보내지 않는다. 아웃박스 스케줄러가 나중에 꺼내서 보낸다.
     * 두 저장을 한 트랜잭션에 묶어서, 배포는 저장됐는데 보낼 것이 없는 일이 생기지 않게 한다(ADR 0004).
     */
    @Transactional
    public Deployment acceptDeployment(Deployment deployment, List<ChunkContent> chunks) {

        // 1. 도메인에 접수를 맡긴다. 뼈대는 SENT, 과목은 WAITING, 배포는 IN_PROGRESS 가 되고
        //    지금 보낼 청크(뼈대 하나)를 돌려받는다. 과목 수 = 전체 청크 - 뼈대 1
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DomainConstants.UTC));
        List<Chunk> toSend = deployment.accept(chunks.size() - 1, now.toInstant());

        // 2. 배포와 모든 청크 내용을 저장한다
        deploymentPersistHelper.persistDeployment(deployment, chunks);

        // 3. 도메인이 보내라고 한 청크만 아웃박스에 넣는다. 과목은 뼈대 결과가 온 뒤에 넣는다(불변식 3)
        for (Chunk chunk : toSend) {
            DeploymentEventPayload deploymentEventPayload = deploymentDataMapper.toDeploymentEventPayload(
                    deployment, chunk.getNo(), chunks.get(chunk.getNo()), now);
            deploymentOutboxHelper.saveDeploymentOutboxMessage(deploymentEventPayload);
        }

        return deployment;
    }

}
