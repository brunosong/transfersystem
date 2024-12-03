package com.brunosong.transfer.system.loadtarget.service;

import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLearningMetaCommand;
import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLearningMetaResponse;
import com.brunosong.transfer.system.loadtarget.service.ports.input.service.CreateLearningMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateLearningMetaServiceImpl implements CreateLearningMetaService {

    private final CreateLearningMetaCommandHandler createLearningMetaCommandHandler;


    public CreateLearningMetaServiceImpl(CreateLearningMetaCommandHandler createLearningMetaCommandHandler) {
        this.createLearningMetaCommandHandler = createLearningMetaCommandHandler;
    }

    @Override
    public CreateLearningMetaResponse createLearningMeta(CreateLearningMetaCommand createLearningMetaCommand) {
        return createLearningMetaCommandHandler.persistLearningMeta(createLearningMetaCommand);
    }

}
