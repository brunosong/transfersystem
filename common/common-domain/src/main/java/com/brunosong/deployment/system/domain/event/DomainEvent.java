package com.brunosong.deployment.system.domain.event;

public interface DomainEvent<T> {
    void fire();
}
