package com.brunosong.transfer.system.ai.service.ports.input.service;

import com.brunosong.transfer.system.ai.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.ai.service.dto.create.CreateLearningMetaResponse;

public interface AiCreateLearningMetaService {

    CreateLearningMetaResponse createLearningMeta(CreateLearningMetaCommand createLearningMetaCommand);

}
