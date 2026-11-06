package com.brunosong.transfer.system.deployment.service.ports.output.repository;

import com.brunosong.transfer.system.deployment.service.entity.SourceConfig;

import java.util.Optional;

public interface SourceConfigRepository {

    Optional<SourceConfig> findSourceConfigInfo(Long sourceConfigId);
}
