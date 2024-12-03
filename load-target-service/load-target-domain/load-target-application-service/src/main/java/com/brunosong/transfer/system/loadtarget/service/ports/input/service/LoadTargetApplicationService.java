package com.brunosong.transfer.system.loadtarget.service.ports.input.service;

import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLoadTargetCommand;
import com.brunosong.transfer.system.loadtarget.service.dto.create.CreateLoadTargetResponse;

public interface LoadTargetApplicationService {
      CreateLoadTargetResponse createLoadTarget(CreateLoadTargetCommand createLoadTargetCommand);
}
