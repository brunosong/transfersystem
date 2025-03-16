package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;

import java.util.Optional;

public interface SourceRepository {
    Optional<SourceContentData> findData(String sourceId, SourceConfig sourceConfig);
}
