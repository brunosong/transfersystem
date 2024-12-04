package com.brunosong.transfer.system.datamigration.service.ports.input.service;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetCommand;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetResponse;

public interface LoadTargetApplicationService {
      CreateLoadTargetResponse createLoadTarget(CreateLoadTargetCommand createLoadTargetCommand);
}
