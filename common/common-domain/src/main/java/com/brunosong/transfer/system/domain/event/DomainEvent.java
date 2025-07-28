package com.brunosong.transfer.system.domain.event;

public interface DomainEvent<T> {
    void fire();
}
