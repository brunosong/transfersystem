package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
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
     * 새 배포를 저장한다. 배포는 Deployment.create 로 이미 다 갖춰져 있다.
     * 배포는 청크 수만 기억한다. 청크 내용과 커리큘럼 ID는 아웃박스 페이로드에 싣는다.
     */
    @Transactional
    public void persistDeployment(Deployment deployment) {
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
