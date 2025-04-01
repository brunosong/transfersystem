package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;


public interface CurriculumRepository {
    long save(SourceContentData sourceContentData);
}
