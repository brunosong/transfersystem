package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
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
    private final ChunkPayloadRepository chunkPayloadRepository;

    /**
     * 새 배포와 모든 청크 내용을 저장한다. 배포는 Deployment.create 로 이미 다 갖춰져 있다.
     * 배포는 청크 수만 기억하고, 내용은 따로 보관한다. 과목은 뼈대 결과가 온 뒤에 이 내용으로 보낸다(불변식 3, 4).
     * 내용 표의 외래 키가 배포를 가리키므로 배포를 먼저 저장한다.
     */
    @Transactional
    public void persistDeployment(Deployment deployment, List<ChunkContent> chunks) {
        saveDeployment(deployment);
        chunkPayloadRepository.saveAll(deployment.getId().getValue(), chunks);
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
