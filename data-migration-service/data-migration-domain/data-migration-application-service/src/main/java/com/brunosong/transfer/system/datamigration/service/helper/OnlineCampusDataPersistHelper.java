package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.DataPersistService;
import com.brunosong.transfer.system.datamigration.service.annotation.TargetType;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
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
@TargetType(
        type = BrunoSongServiceType.BRUNOSONG_ONLINE_CAMPUS
)
public class OnlineCampusDataPersistHelper implements DataPersistService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumCachePort curriculumCachePort;

    @Transactional
    public long onlineCampusPersist(SourceContentData sourceContentData) {
        DataMigrationDto dataMigrationDto = curriculumRepository.saveOrUpdate(sourceContentData);

        curriculumCachePort.save(dataMigrationDto.id(), dataMigrationDto.curriculumId());
        log.info("Successfully saved content with id: {}", dataMigrationDto.id());

        return dataMigrationDto.id();
    }

    @Override
    public void dataPersist(DataMigration dataMigrationInfo, SourceContentData sourceContentData) {

    }
}
