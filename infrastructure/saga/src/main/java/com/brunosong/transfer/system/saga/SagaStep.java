package com.brunosong.transfer.system.saga;

import com.brunosong.transfer.system.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, R extends DomainEvent> {
    S process(T data);
    R rollback(T data);
}
