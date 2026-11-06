package com.brunosong.deployment.system.datamigration.service;

import com.brunosong.deployment.system.datamigration.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;

public interface DataPersistService {

    AppliedContent dataPersist(TargetSystemEnvironment environment, String curriculumId, ChunkContent content);
}
