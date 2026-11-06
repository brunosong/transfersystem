package com.brunosong.deployment.system.targetagent.service.dataaccess.target.mapper;

import com.brunosong.deployment.system.targetagent.service.dataaccess.target.entity.TargetEntity;
import com.brunosong.deployment.system.targetagent.service.domain.entity.Target;
import org.springframework.stereotype.Component;

@Component
public class
TargetDataAccessMapper {

    public Target entityToDomain(TargetEntity entity) {
        return Target.builder()
                .targetSystem(entity.getTargetSystem())
                .targetSystemEnvironment(entity.getTargetSystemEnvironment())
                .build();
    }
}
