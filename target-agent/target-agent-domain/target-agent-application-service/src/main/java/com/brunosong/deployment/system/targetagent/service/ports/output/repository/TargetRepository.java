package com.brunosong.deployment.system.targetagent.service.ports.output.repository;

import com.brunosong.deployment.system.targetagent.service.domain.entity.Target;

import java.util.Optional;

public interface TargetRepository {
    Optional<Target> findById(Long targetId);
}
