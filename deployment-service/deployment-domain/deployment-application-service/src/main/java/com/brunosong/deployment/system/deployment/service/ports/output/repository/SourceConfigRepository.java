package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;

import java.util.Optional;

public interface SourceConfigRepository {

    Optional<SourceConfig> findSourceConfigInfo(Long sourceConfigId);
}
