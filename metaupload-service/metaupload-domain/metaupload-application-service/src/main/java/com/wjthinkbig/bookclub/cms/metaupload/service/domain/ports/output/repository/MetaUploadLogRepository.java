package com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaUploadLog;

import java.util.Optional;

public interface MetaUploadLogRepository {
    Optional<MetaUploadLog> save(MetaUploadLog metaUploadLog);
}
