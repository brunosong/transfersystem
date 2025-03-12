package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;

import java.util.Optional;

public interface SourceConfigRepository {

    Optional<SourceConfig> findSourceConfigInfo(Long sourceConfigId);
}
