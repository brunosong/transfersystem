package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;
import com.brunosong.transfer.system.datamigration.service.ports.output.cache.CurriculumCachePort;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPersistHelper {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumCachePort curriculumCachePort;

    public long persist(SourceContentData sourceContentData) {
        DataMigrationDto dataMigrationDto = curriculumRepository.saveOrUpdate(sourceContentData);

        curriculumCachePort.save(dataMigrationDto.id(), dataMigrationDto.curriculumId());
        log.info("Successfully saved content with id: {}", dataMigrationDto.id());

        return dataMigrationDto.id();
    }

}
