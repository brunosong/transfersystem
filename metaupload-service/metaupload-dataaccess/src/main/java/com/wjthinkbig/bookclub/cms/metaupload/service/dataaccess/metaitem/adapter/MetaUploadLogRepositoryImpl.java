package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.metaitem.adapter;

import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.metaitem.mapper.MetaItemDataAccessMapper;
import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.metaitem.repository.MetaUploadLogJpaRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository.MetaUploadLogRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaUploadLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class MetaUploadLogRepositoryImpl implements MetaUploadLogRepository {

    private final MetaUploadLogJpaRepository metaUploadLogJpaRepository;
    private final MetaItemDataAccessMapper metaItemDataAccessMapper;

    public MetaUploadLogRepositoryImpl(MetaUploadLogJpaRepository metaUploadLogJpaRepository,
                                       MetaItemDataAccessMapper metaItemDataAccessMapper) {
        this.metaUploadLogJpaRepository = metaUploadLogJpaRepository;
        this.metaItemDataAccessMapper = metaItemDataAccessMapper;
    }

    @Override
    public Optional<MetaUploadLog> save(MetaUploadLog metaUploadLog) {
        return Optional.of(metaItemDataAccessMapper.metaUploadLogEntityToMetaUploadLog(
                                metaUploadLogJpaRepository.save(
                                        metaItemDataAccessMapper.metaUploadLogToMetaUploadLogEntity(metaUploadLog)
                                )
                            ));
    }
}
