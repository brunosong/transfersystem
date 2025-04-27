package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;


public interface CurriculumRepository {
    DataMigrationDto saveOrUpdate(SourceContentData sourceContentData);
}
