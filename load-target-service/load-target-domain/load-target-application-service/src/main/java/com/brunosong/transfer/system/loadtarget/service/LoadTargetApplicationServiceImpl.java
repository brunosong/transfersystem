package com.brunosong.transfer.system.loadtarget.service;

import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLoadTargetCommand;
import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLoadTargetResponse;
import com.brunosong.transfer.system.loadtarget.service.ports.input.service.LoadTargetApplicationService;
import org.springframework.stereotype.Service;

@Service
public class LoadTargetApplicationServiceImpl implements LoadTargetApplicationService {

    @Override
    public CreateLoadTargetResponse createLoadTarget(CreateLoadTargetCommand createLoadTargetCommand) {
        return null;
    }
}
