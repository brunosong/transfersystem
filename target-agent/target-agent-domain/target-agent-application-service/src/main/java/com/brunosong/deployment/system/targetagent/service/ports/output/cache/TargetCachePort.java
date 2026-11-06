package com.brunosong.deployment.system.targetagent.service.ports.output.cache;

import com.brunosong.deployment.system.targetagent.service.domain.entity.Target;

import java.util.Optional;

public interface TargetCachePort {
    void save(Target target);

    Optional<Target> findById(Long migrationInfoId);
}
