package com.brunosong.transfer.system.deployment.service.ports.output.repository;

import com.brunosong.transfer.system.deployment.service.entity.SourceConfig;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;

import java.util.Optional;

public interface SourceRepository {
    Optional<SourceContentData> findData(String sourceId, SourceConfig sourceConfig);
}
