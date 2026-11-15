package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.deployment.system.deployment.service.handler.DeploymentResultHandler;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.ports.input.message.listener.ApplyResultMessageListener;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentCachePort;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplyResultMessageListenerImpl implements ApplyResultMessageListener {

    private final DeploymentResultHandler deploymentResultHandler;

    /** 결과 하나를 배포에 기록한다. 처리 중 예외가 나면 Kafka 리스너가 다시 시도하고, 끝내 안 되면 DLT 로 보낸다 */
    @Override
    public void deploymentStatusUpdate(ApplyResultMessage applyResultMessage) {
        deploymentResultHandler.applyResult(applyResultMessage);
    }
}
