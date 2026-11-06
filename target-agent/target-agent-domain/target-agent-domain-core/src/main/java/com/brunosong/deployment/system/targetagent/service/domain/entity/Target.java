package com.brunosong.deployment.system.targetagent.service.domain.entity;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.deployment.system.domain.entity.AggregateRoot;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Target extends AggregateRoot<TargetId> {

    private String targetSystem;
    private TargetSystemEnvironment targetSystemEnvironment;

}
