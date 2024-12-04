package com.brunosong.transfer.system.datamigration.service.ports.input.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLearningMetaResponse;

public interface CreateLearningMetaService {

    CreateLearningMetaResponse createLearningMeta(CreateLearningMetaCommand createLearningMetaCommand);

}
