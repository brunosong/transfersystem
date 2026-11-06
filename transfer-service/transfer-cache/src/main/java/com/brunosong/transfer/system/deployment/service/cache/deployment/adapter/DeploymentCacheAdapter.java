package com.brunosong.transfer.system.deployment.service.cache.deployment.adapter;

import com.brunosong.transfer.system.deployment.service.cache.deployment.entity.DeploymentCacheEntity;
import com.brunosong.transfer.system.deployment.service.cache.deployment.mapper.DeploymentCacheMapper;
import com.brunosong.transfer.system.deployment.service.cache.deployment.repository.DeploymentRedisRepository;
import com.brunosong.transfer.system.deployment.service.entity.Deployment;
import com.brunosong.transfer.system.deployment.service.ports.output.repository.DeploymentCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeploymentCacheAdapter implements DeploymentCachePort {

    private final DeploymentCacheMapper deploymentCacheMapper;
    private final DeploymentRedisRepository deploymentRedisRepository;

    @Override
    public Optional<Deployment> getDeploymentInfo(String deploymentId) {
        log.info("Checking Redis cache for deploymentId: {}", deploymentId);
        return deploymentRedisRepository.findById(deploymentId)
                .map(deploymentCacheMapper::toDeployment);
    }

    @Override
    public void saveDeploymentId(String deploymentId, int totalChunkSize) {
        log.info("Saving to Redis cache for deploymentId: {}", deploymentId);
        DeploymentCacheEntity entity = DeploymentCacheEntity.builder()
                .deploymentId(deploymentId)
                .totalChunkSize(totalChunkSize)
                .build();
        deploymentRedisRepository.save(entity);
    }
}
