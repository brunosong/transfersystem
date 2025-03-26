package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPersistHelper {

    private final CurriculumRepository curriculumRepository;

    public String persist(SourceContentData sourceContentData) {
        curriculumRepository.save(sourceContentData);
        return "SUCCESS";
    }


}
