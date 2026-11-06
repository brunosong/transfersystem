package com.brunosong.transfer.system.deployment.service;

import com.brunosong.transfer.system.deployment.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.deployment.service.entity.Deployment;
import com.brunosong.transfer.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.transfer.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.transfer.system.deployment.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.deployment.service.ports.output.repository.DeploymentCachePort;
import com.brunosong.transfer.system.deployment.service.ports.output.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataMigrationMessageListenerImpl implements DataMigrationMessageListener {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentDataMapper deploymentDataMapper;
    private final DeploymentCachePort deploymentCachePort;

    @Override
    @Transactional
    public void deploymentStatusUpdate(DataMigrationResponse dataMigrationResponse) {
        Deployment deployment = deploymentDataMapper.dataMigrationResponseToDeployment(dataMigrationResponse);

        if (dataMigrationResponse.getChunkOffset() == 0) {
            deploymentRepository.updateDeploymentStatus(deployment);

            Deployment result = deploymentRepository.findById(deployment.getId().getValue()).orElseThrow(() -> {
                throw new DeploymentNotFoundException("deploymentId is " + deployment.getId().getValue());
            });

            deploymentCachePort.saveDeploymentId(deployment.getId().getValue().toString(),
                                                result.getTotalChunkSize());
        } else {
            Optional<Deployment> deploymentInfo =
                    deploymentCachePort.getDeploymentInfo(deployment.getId().getValue().toString());

            // 캐시 미스 시 DB 조회
            if (deploymentInfo.isEmpty()) {
                deploymentInfo = deploymentRepository.findById(deployment.getId().getValue());
            }

            dataMigrationResponse.updateTotalChunkSize(deploymentInfo.get().getTotalChunkSize());
            deploymentRepository.saveChunk(dataMigrationResponse);
        }
    }
}
