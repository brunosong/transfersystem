package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;

import com.brunosong.deployment.system.domain.DomainConstants;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.event.DeploymentEvent;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeploymentDomainServiceImpl implements DeploymentDomainService {

    /**
     * 커리큘럼을 청크로 나누고 배포를 시작 상태로 만든 뒤, 청크마다 보낼 이벤트를 하나씩 만든다.
     * 배포는 청크 수만 기억하고, 청크 내용과 커리큘럼 ID는 이벤트에 싣는다.
     */
    @Override
    public List<DeploymentRequestEvent> validateAndInitiateDeployment(Deployment deployment, SourceConfig sourceConfig,
                                                                      Curriculum curriculum) {
        List<ChunkContent> chunks = ChunkContent.split(curriculum);

        deployment.validateInitialDeployment();
        deployment.initializeDeployment(sourceConfig.getSourceType(), sourceConfig.getDbType(), chunks.size());

        // 도메인 객체는 같고 내용과 청크 순서만 다른 DeploymentRequestEvent 를 만든다
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DomainConstants.UTC));
        List<DeploymentRequestEvent> deploymentRequestEventList = new ArrayList<>();
        for (int chunkOffset = 0; chunkOffset < chunks.size(); chunkOffset++) {
            deploymentRequestEventList.add(
                new DeploymentRequestEvent(deployment, now, chunkOffset, curriculum.curriculumId(), chunks.get(chunkOffset))
            );
        }
        return deploymentRequestEventList;
    }

    @Override
    public void markSent(Deployment deployment) {
        log.info("Deployment with id: {} is markSent", deployment.getId().getValue());
        deployment.markSent();
    }

    @Override
    public void markProcessed(Deployment deployment) {
        log.info("Deployment with id: {} is markProcessed", deployment.getId().getValue());
        deployment.markProcessed();
    }

    @Override
    public void markSuccess(Deployment deployment) {
        log.info("Deployment with id: {} is markSuccess", deployment.getId().getValue());
        deployment.markSuccess();
    }

    @Override
    public void markFailed(Deployment deployment) {
        log.info("Deployment with id: {} is markFailed", deployment.getId().getValue());
        deployment.markFailed();
    }
}
