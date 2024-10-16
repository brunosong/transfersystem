package com.brunosong.transfer.system.ai.service;

import com.brunosong.transfer.system.ai.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.ai.service.dto.create.CreateLearningMetaResponse;
import com.brunosong.transfer.system.ai.service.ports.input.service.AiCreateLearningMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiCreateLearningMetaServiceImpl implements AiCreateLearningMetaService {

    private final CreateLearningMetaCommandHandler createLearningMetaCommandHandler;


    public AiCreateLearningMetaServiceImpl(CreateLearningMetaCommandHandler createLearningMetaCommandHandler) {
        this.createLearningMetaCommandHandler = createLearningMetaCommandHandler;
    }

    @Override
    public CreateLearningMetaResponse createLearningMeta(CreateLearningMetaCommand createLearningMetaCommand) {
        return createLearningMetaCommandHandler.persistLearningMeta(createLearningMetaCommand);
    }

}
