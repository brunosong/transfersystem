package com.brunosong.transfer.system.datamigration.service.mapper;

import com.brunosong.transfer.system.domain.valueobject.DatabaseEnvironment;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetServiceType;
import com.brunosong.transfer.system.domain.valueobject.LoadTargetTypeStatus;
import com.brunosong.transfer.system.datamigration.service.domain.entity.LoadTarget;
import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLoadTargetCommand;
import org.springframework.stereotype.Component;

@Component
public class LoadTargetDataMapper {

    public LoadTarget createLoadTargetCommandToLoadTarget(CreateLoadTargetCommand command) {
        return LoadTarget.builder()
                .targetDb(DatabaseEnvironment.valueOf(command.getTargetDb()))
                .targetService(LoadTargetServiceType.valueOf(command.getTargetService()))
                .type(LoadTargetTypeStatus.valueOf(command.getType()))
                .build();
    }
}
