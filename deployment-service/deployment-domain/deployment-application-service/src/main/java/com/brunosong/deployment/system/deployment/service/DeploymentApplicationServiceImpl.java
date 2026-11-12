package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.deployment.service.dto.create.CreateDeploymentResponse;
import com.brunosong.deployment.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.handler.SourceFindDataHandler;
import com.brunosong.deployment.system.deployment.service.handler.DeploymentExecutionHandler;
import com.brunosong.deployment.system.deployment.service.helper.source.SourceConfigHelper;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.ports.input.service.DeploymentApplicationService;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class DeploymentApplicationServiceImpl implements DeploymentApplicationService {

    private final DeploymentExecutionHandler deploymentExecutionHandler;
    private final DeploymentDataMapper deploymentDataMapper;
    private final SourceConfigHelper sourceConfigHelper;
    private final SourceFindDataHandler sourceFindDataHandler;

    @Override
    public CreateDeploymentResponse executeDeployment(DeploymentRequest deploymentRequest) {

        Deployment deployment =
                deploymentDataMapper.deploymentRequestToDeployment(deploymentRequest);

        // 1. 어느 원본 저장소에서 읽을지 찾는다
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(deployment.getSourceConfigId());

        // 2. 원본 문서를 꺼내 커리큘럼으로 바꾼다
        Curriculum curriculum = sourceFindDataHandler.findCurriculum(deployment.getSourceId(), sourceConfig);

        // 3. 커리큘럼을 청크로 나눠 배포를 접수하고 아웃박스에 넣는다
        Deployment sent = deploymentExecutionHandler.sendData(deployment, sourceConfig, curriculum);

        return new CreateDeploymentResponse(sent.getId().getValue(), sent.getTotalChunkSize());
    }
}
