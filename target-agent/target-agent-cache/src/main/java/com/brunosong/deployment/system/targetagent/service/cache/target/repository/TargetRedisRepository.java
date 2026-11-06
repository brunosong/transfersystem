package com.brunosong.deployment.system.targetagent.service.cache.target.repository;

import com.brunosong.deployment.system.targetagent.service.cache.target.entity.TargetCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface TargetRedisRepository extends CrudRepository<TargetCacheEntity, Long> {
}
