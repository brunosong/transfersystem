package com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository;

import com.brunosong.transfer.system.metaupload.service.entity.MetaUploadLog;

import java.util.Optional;

public interface MetaUploadLogRepository {
    Optional<MetaUploadLog> save(MetaUploadLog metaUploadLog);
}
