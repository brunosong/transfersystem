package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.deployment.system.deployment.service.helper.deployment.DeploymentPersistHelper;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.scheduler.DeploymentOutboxHelper;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    public Deployment acceptDeployment(Deployment deployment, SourceConfig sourceConfig,
                                       String curriculumId, List<ChunkContent> chunks) {

        // 1. 배포를 시작 상태로 저장하고, 청크마다 이벤트를 하나씩 만든다
        List<DeploymentRequestEvent> deploymentRequestEvents =
                deploymentPersistHelper.persistDeployment(deployment, sourceConfig, curriculumId, chunks);

        // 2. 청크마다 아웃박스 행을 하나씩 적는다
        for (DeploymentRequestEvent deploymentRequestEvent : deploymentRequestEvents) {

            // 이벤트를 아웃박스에 적을 모양(페이로드)으로 바꾼다
            DeploymentEventPayload deploymentEventPayload =
                    deploymentDataMapper.deploymentRequestEventToDeploymentEventPayload(deploymentRequestEvent);

            deploymentOutboxHelper.saveDeploymentOutboxMessage(deploymentEventPayload,
                    UUID.randomUUID());

        }

        return deployment;
    }

}
