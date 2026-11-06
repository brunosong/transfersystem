package com.brunosong.transfer.system.deployment.service;

import com.brunosong.transfer.system.deployment.service.dto.create.CreateDeploymentResponse;
import com.brunosong.transfer.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.transfer.system.deployment.service.entity.Deployment;
import com.brunosong.transfer.system.deployment.service.handler.SourceFindDataHandler;
import com.brunosong.transfer.system.deployment.service.handler.DeploymentExecutionHandler;
import com.brunosong.transfer.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.transfer.system.deployment.service.ports.input.service.DeploymentApplicationService;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;
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
    private final SourceFindDataHandler sourceFindDataHandler;

    @Override
    public CreateDeploymentResponse executeDeployment(DeploymentRequest deploymentRequest) {

        Deployment deployment =
                deploymentDataMapper.deploymentRequestToDeployment(deploymentRequest);

        // 1. 데이터 배포를 위한 소스 데이터 조회
        SourceContentData sourceContentData = sourceFindDataHandler.findSourceContentData(deployment.getSourceConfigId(), deployment.getSourceId());

        // 2. 데이터 배포
        Deployment sent = deploymentExecutionHandler.sendData(deployment, sourceContentData);

        return new CreateDeploymentResponse(sent.getId().getValue(),
                                          sourceContentData.getChunkDataList().size());
    }
}
