package com.brunosong.deployment.system.domain.valueobject;

import java.util.UUID;

public class DeploymentId extends BaseId<UUID> {

    public DeploymentId(UUID value) {
        super(value);
    }
}
