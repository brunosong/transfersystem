package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetResponse;
import com.brunosong.transfer.system.datamigration.service.ports.input.service.LoadTargetApplicationService;
import org.springframework.stereotype.Service;

@Service
public class LoadTargetApplicationServiceImpl implements LoadTargetApplicationService {

    @Override
    public CreateLoadTargetResponse createLoadTarget(CreateLoadTargetCommand createLoadTargetCommand) {
        return null;
    }
}
