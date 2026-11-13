package com.brunosong.deployment.system.deployment.service.handler;

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
    public Deployment acceptDeployment(Deployment deployment, String curriculumId, List<ChunkContent> chunks) {

        // 1. 배포와 모든 청크 내용을 저장한다
        deploymentPersistHelper.persistDeployment(deployment, chunks);

        // 2. 청크마다 아웃박스 행을 하나씩 적는다. 목록의 순서가 청크 번호다
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DomainConstants.UTC));
        for (int chunkOffset = 0; chunkOffset < chunks.size(); chunkOffset++) {

            DeploymentEventPayload deploymentEventPayload = deploymentDataMapper.toDeploymentEventPayload(
                    deployment, curriculumId, chunkOffset, chunks.get(chunkOffset), now);

            deploymentOutboxHelper.saveDeploymentOutboxMessage(deploymentEventPayload);
        }

        return deployment;
    }

}
