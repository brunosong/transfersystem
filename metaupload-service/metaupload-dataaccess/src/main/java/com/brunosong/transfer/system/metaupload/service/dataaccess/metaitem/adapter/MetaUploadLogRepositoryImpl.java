package com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.adapter;

import com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.mapper.MetaItemDataAccessMapper;
import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.MetaUploadLogRepository;
import com.brunosong.transfer.system.metaupload.service.entity.MetaUploadLog;
import com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.repository.MetaUploadLogJpaRepository;
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
