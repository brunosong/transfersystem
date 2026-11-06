package com.brunosong.transfer.system.deployment.service.cache.deployment.repository;

import com.brunosong.transfer.system.deployment.service.cache.deployment.entity.DeploymentCacheEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRedisRepository extends CrudRepository<DeploymentCacheEntity, String> {
}
