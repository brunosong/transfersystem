package com.brunosong.deployment.system.deployment.service.event;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;

import java.time.ZonedDateTime;

public abstract class DeploymentEvent {

    private final Deployment deployment;
    private final ZonedDateTime createdAt;

    public DeploymentEvent(Deployment deployment, ZonedDateTime createdAt) {
        this.deployment = deployment;
        this.createdAt = createdAt;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
