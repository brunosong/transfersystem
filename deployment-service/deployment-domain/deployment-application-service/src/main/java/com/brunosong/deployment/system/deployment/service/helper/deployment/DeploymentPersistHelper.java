package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.domain.DomainConstants;
import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentPersistHelper {

    private final DeploymentRepository deploymentRepository;

    /**
     * 배포를 시작 상태로 만들어 저장하고, 청크마다 보낼 이벤트를 하나씩 만든다.
     * 배포는 청크 수만 기억하고, 청크 내용과 커리큘럼 ID는 이벤트에 싣는다.
     */
    @Transactional
    public List<DeploymentRequestEvent> persistDeployment(Deployment deployment, SourceConfig sourceConfig,
                                                          String curriculumId, List<ChunkContent> chunks) {

        // 1. 새 배포인지 확인하고 시작 상태로 만든다
        deployment.validateInitialDeployment();
        deployment.initializeDeployment(sourceConfig.getSourceType(), sourceConfig.getDbType(), chunks.size());
        List<DeploymentRequestEvent> deploymentRequestEvents = toRequestEvents(deployment, curriculumId, chunks);

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

    /** 같은 배포에 내용과 청크 번호만 다른 이벤트를 만든다. 목록의 순서가 청크 번호다 */
    private List<DeploymentRequestEvent> toRequestEvents(Deployment deployment, String curriculumId,
                                                         List<ChunkContent> chunks) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DomainConstants.UTC));
        List<DeploymentRequestEvent> events = new ArrayList<>();
        for (int chunkOffset = 0; chunkOffset < chunks.size(); chunkOffset++) {
            events.add(new DeploymentRequestEvent(deployment, now, chunkOffset, curriculumId, chunks.get(chunkOffset)));
        }
        return events;
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
