package com.brunosong.deployment.system.deployment.service.cache.deployment.repository;

import com.brunosong.deployment.system.deployment.service.cache.deployment.entity.DeploymentCacheEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRedisRepository extends CrudRepository<DeploymentCacheEntity, String> {
}
