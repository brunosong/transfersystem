package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPersistHelper {

    private final CurriculumRepository curriculumRepository;

    public String persist(SourceContentData sourceContentData) {
        long id = curriculumRepository.save(sourceContentData);
        log.info("Successfully saved content with id: {}", id);
        return "SUCCESS";
    }

}
