package com.brunosong.transfer.system.loadtarget.service.ports.input.service;

import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLearningMetaResponse;

public interface CreateLearningMetaService {

    CreateLearningMetaResponse createLearningMeta(CreateLearningMetaCommand createLearningMetaCommand);

}
