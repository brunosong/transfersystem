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


    @Transactional
    public Deployment sendData(Deployment deployment, SourceConfig sourceConfig,
                               String curriculumId, List<ChunkContent> chunks) {

        // 1. 데이터 배포 전 로그 저장
        List<DeploymentRequestEvent> deploymentRequestEvents =
                deploymentPersistHelper.persistDeployment(deployment, sourceConfig, curriculumId, chunks);

        for (DeploymentRequestEvent deploymentRequestEvent : deploymentRequestEvents) {

            // 데이터 배포 이벤트 페이로드 생성
            DeploymentEventPayload deploymentEventPayload =
                    deploymentDataMapper.deploymentRequestEventToDeploymentEventPayload(deploymentRequestEvent);

            // 데이터 배포 아웃박스 메시지 저장
            deploymentOutboxHelper.saveDeploymentOutboxMessage(deploymentEventPayload,
                    UUID.randomUUID());

        }

        // 2. 데이터 배포 결과 업데이트
        updateDeploymentSendResult(deployment);

        return deployment;
    }


    public void updateDeploymentSendResult(Deployment deployment) {
        deploymentPersistHelper.updateDeploymentSendResult(deployment);
    }

}
