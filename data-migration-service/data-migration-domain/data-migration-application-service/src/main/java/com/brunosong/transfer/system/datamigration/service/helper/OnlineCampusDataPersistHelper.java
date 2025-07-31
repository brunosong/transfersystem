package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.DataPersistService;
import com.brunosong.transfer.system.datamigration.service.annotation.ServiceTypeSelector;
import com.brunosong.transfer.system.datamigration.service.config.OnlineCampusRoutingDataSourceContextHolder;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DataSourceType;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;
import com.brunosong.transfer.system.datamigration.service.ports.output.cache.CurriculumCachePort;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import com.brunosong.transfer.system.domain.valueobject.BrunoSongServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ServiceTypeSelector(
        type = BrunoSongServiceType.BRUNOSONG_ONLINE_CAMPUS
)
public class OnlineCampusDataPersistHelper implements DataPersistService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumCachePort curriculumCachePort;

    @Override
    public long dataPersist(DataMigration dataMigrationInfo, SourceContentData sourceContentData) {

        TargetSystemEnvironment targetSystemEnvironment = dataMigrationInfo.getTargetSystemEnvironment();
        try {
            if (targetSystemEnvironment == TargetSystemEnvironment.PROD) {
                log.info("Persisting data for Online Campus in PROD environment");
                OnlineCampusRoutingDataSourceContextHolder.setDataSourceType(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_PROD);
            } else {
                OnlineCampusRoutingDataSourceContextHolder.setDataSourceType(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_DEV);
                log.info("Persisting data for Online Campus in DEV environment");
            }
            return onlineCampusDataMigration(sourceContentData);

        } finally {
            OnlineCampusRoutingDataSourceContextHolder.clearDataSourceType();
        }
    }

    @Transactional
    public long onlineCampusDataMigration(SourceContentData sourceContentData) {
        DataMigrationDto dataMigrationDto = curriculumRepository.saveOrUpdate(sourceContentData);
        log.info("Successfully saved content with id: {}", dataMigrationDto.id());

        curriculumCachePort.save(dataMigrationDto.id(), dataMigrationDto.curriculumId());
        log.info("Successfully cached content with id: {}", dataMigrationDto.id());

        return dataMigrationDto.id();
    }
}
