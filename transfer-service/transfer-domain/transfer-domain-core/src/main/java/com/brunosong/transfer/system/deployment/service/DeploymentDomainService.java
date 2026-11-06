package com.brunosong.transfer.system.deployment.service;

import com.brunosong.transfer.system.deployment.service.entity.Deployment;
import com.brunosong.transfer.system.deployment.service.event.DeploymentEvent;
import com.brunosong.transfer.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;

import java.time.LocalDateTime;
import java.util.List;

public interface DeploymentDomainService {

    List<DeploymentRequestEvent> validateAndInitiateDeployment(Deployment deployment, SourceContentData sourceContentData);

    void markSent(Deployment deployment);

    void markProcessed(Deployment deployment);

    void markSuccess(Deployment deployment);

    void markFailed(Deployment deployment);

}
