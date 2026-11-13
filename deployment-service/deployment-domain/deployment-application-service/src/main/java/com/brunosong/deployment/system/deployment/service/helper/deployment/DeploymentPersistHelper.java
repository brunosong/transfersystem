package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentPersistHelper {

    private final DeploymentRepository deploymentRepository;

    /**
     * 새 배포인지 확인하고 시작 상태로 만들어 저장한다.
     * 배포는 청크 수만 기억한다. 청크 내용과 커리큘럼 ID는 아웃박스 페이로드에 싣는다.
     */
    @Transactional
    public void persistDeployment(Deployment deployment, SourceConfig sourceConfig, int totalChunkSize) {
        deployment.validateInitialDeployment();
        deployment.initializeDeployment(sourceConfig.getSourceType(), sourceConfig.getDbType(), totalChunkSize);
        saveDeployment(deployment);
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
