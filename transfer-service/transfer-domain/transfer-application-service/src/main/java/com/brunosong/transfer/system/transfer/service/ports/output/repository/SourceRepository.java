package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;

import java.util.Optional;

public interface SourceRepository {
    Optional<SourceContentData> findData(String sourceId, SourceType sourceType);
}
