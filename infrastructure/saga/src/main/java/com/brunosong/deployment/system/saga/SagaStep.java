package com.brunosong.deployment.system.saga;

import com.brunosong.deployment.system.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, R extends DomainEvent> {
    S process(T data);
    R rollback(T data);
}
