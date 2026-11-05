package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;

public interface CurriculumRepository {

    DataMigrationDto saveOrUpdate(String curriculumId, ChunkContent content);
}
