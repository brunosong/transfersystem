package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetResponse;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.LoadTargetRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoadTargetCreateHandler {

    private final LoadTargetRepository loadTargetRepository;

    public LoadTargetCreateHandler(LoadTargetRepository loadTargetRepository) {
        this.loadTargetRepository = loadTargetRepository;
    }

    @Transactional
    public CreateLoadTargetResponse persist(CreateLoadTargetCommand createLoadTargetCommand) {

        loadTargetRepository.save()

    }
}
