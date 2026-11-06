package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;

import com.brunosong.deployment.system.domain.DomainConstants;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.event.DeploymentEvent;
import com.brunosong.deployment.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeploymentDomainServiceImpl implements DeploymentDomainService {

    @Override
    public List<DeploymentRequestEvent> validateAndInitiateDeployment(Deployment deployment, SourceContentData sourceContentData) {
        deployment.validateInitialDeployment();
        deployment.initializeDeployment(sourceContentData);

        // 도메인 객채는 같고 데이터와 청크 순서만 다른 DeploymentRequestEvent 객체를 생성하여 반환
        List<DeploymentRequestEvent> deploymentRequestEventList = new ArrayList<>();
        List<ChunkContent> chunkDataList = sourceContentData.getChunkDataList();
        for (int chunkOffset = 0; chunkOffset < chunkDataList.size(); chunkOffset++) {
            // 청크 데이터 유효성 검사
            deploymentRequestEventList.add(
                new DeploymentRequestEvent(deployment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), chunkOffset, chunkDataList.get(chunkOffset))
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
