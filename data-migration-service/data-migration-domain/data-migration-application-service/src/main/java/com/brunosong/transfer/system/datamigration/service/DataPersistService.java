package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.AppliedContent;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;

public interface DataPersistService {

    AppliedContent dataPersist(TargetSystemEnvironment environment, String curriculumId, ChunkContent content);
}
