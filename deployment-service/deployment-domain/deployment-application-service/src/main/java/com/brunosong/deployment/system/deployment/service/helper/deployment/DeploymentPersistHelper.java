package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.deployment.service.DeploymentDomainService;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentPersistHelper {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentDomainService deploymentDomainService;

    @Transactional
    public List<DeploymentRequestEvent> persistDeployment(Deployment deployment, SourceContentData sourceContentData) {

        // 1. 데이터 유효성 검사 및 이벤트 객체 생성
        List<DeploymentRequestEvent> deploymentRequestEvents = deploymentDomainService.validateAndInitiateDeployment(deployment, sourceContentData);

        // 2. 배포 로그 저장
        Deployment deploymentResult = saveDeployment(deployment);

        // 3. 이벤트 객체에 배포 ID 설정
        DeploymentId deploymentId = deploymentResult.getId();
        deploymentRequestEvents.forEach(event -> event.getDeployment().setId(deploymentId));

        return deploymentRequestEvents;

    }

    @Transactional
    public void updateDeploymentSendResult(Deployment deployment) {
        deploymentRepository.updateDeploymentSendResult(deployment);
        log.info("UpdateDeploymentSendResult deploymentId is {}", deployment.getId());
    }

    private Deployment saveDeployment(Deployment deployment) {

        Deployment deploymentResult = deploymentRepository.save(deployment);

        if (deploymentResult == null) {
            log.error("Could not save deployment!");
            throw new DeploymentDomainException("Could not save deployment!");
        }
        return deploymentResult;
    }
}
